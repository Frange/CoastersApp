package com.frange.coasters.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.usecase.RequestAllParkInfoListUseCase
import com.frange.coasters.domain.usecase.RequestParkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestCompanyListUseCase: RequestAllParkInfoListUseCase,
    private val requestParkUseCase: RequestParkUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ParkUiState>(ParkUiState.Loading)
    val uiState: StateFlow<ParkUiState> = _uiState.asStateFlow()
    var isFirstTime = true

    private var fetchJob: kotlinx.coroutines.Job? = null

    init {
        requestAllParkInfoList()
    }

    fun requestAllParkInfoList() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = ParkUiState.Loading

            requestCompanyListUseCase.execute()
                .catch { e ->
                    _uiState.value = ParkUiState.Error(e.message ?: "Error de conexión")
                }
                .collect { result ->
                    val rawData = result.data as? List<Any?> ?: emptyList()
                    val allParks = mutableListOf<ParkInfo>()
                    rawData.forEach { item ->
                        when (item) {
                            is Company -> item.parks?.let { allParks.addAll(it) }
                            is ParkInfo -> allParks.add(item)
                        }
                    }

                    val cleanParks = allParks.filter { it.id != null }.distinctBy { it.id }

                    if (cleanParks.isNotEmpty()) {
                        _uiState.value = ParkUiState.Success(
                            availableParks = cleanParks,
                            selectedPark = null,
                            isRefreshing = true
                        )
                        cleanParks.first().id?.let { launchParkRequest(it) }
                    } else if (isFirstTime) {
                        isFirstTime = false
                        _uiState.value = ParkUiState.Loading
                    } else {
                        _uiState.value = ParkUiState.Error("No se encontraron parques")
                    }
                }
        }
    }

    fun requestPark(parkId: Int) {
        val currentState = _uiState.value as? ParkUiState.Success ?: return
        _uiState.value = currentState.copy(isRefreshing = true)
        launchParkRequest(parkId)
    }

    private fun launchParkRequest(id: Int) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            requestParkUseCase.execute(RequestParkUseCase.Parameters(id))
                .catch { _ ->
                    val lastState = _uiState.value as? ParkUiState.Success
                    if (lastState != null) {
                        _uiState.value = lastState.copy(isRefreshing = false)
                    }
                }
                .collect { result ->
                    val lastState = _uiState.value as? ParkUiState.Success
                    if (lastState != null) {
                        _uiState.value = lastState.copy(
                            selectedPark = result.data,
                            isRefreshing = false
                        )
                    }
                }
        }
    }
}