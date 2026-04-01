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

    init {
        requestAllParkInfoList()
    }

    fun requestAllParkInfoList() {
        viewModelScope.launch {
            _uiState.value = ParkUiState.Loading

            requestCompanyListUseCase.execute()
                .catch { e ->
                    Log.e("MY_TAG", "Error en UseCase", e)
                    _uiState.value = ParkUiState.Error(e.message ?: "Error")
                }
                .collect { result ->
                    val rawData = result.data as? List<Any?> ?: emptyList()

                    val sortedParks = mutableListOf<ParkInfo>()

                    rawData.forEach { item ->
                        when (item) {
                            is Company -> {
                                item.parks?.let { sortedParks.addAll(it) }
                            }

                            is ParkInfo -> {
                                sortedParks.add(item)
                            }
                        }
                    }

                    if (sortedParks.isNotEmpty()) {
                        _uiState.value = ParkUiState.Success(
                            availableParks = sortedParks,
                            isRefreshing = false
                        )

                        sortedParks.firstOrNull()?.id?.let { requestPark(it) }
                    } else {
                        _uiState.value = ParkUiState.Error("No se encontraron parques")
                    }
                }
        }
    }

    fun requestPark(parkId: Int) {
        val currentState = _uiState.value as? ParkUiState.Success ?: return
        val selectedParkInfo = currentState.availableParks.find { it.id == parkId }

        if (selectedParkInfo != null) {
            launchParkRequest(parkId)
        }
    }

    private fun launchParkRequest(id: Int) {
        val currentState = _uiState.value
        if (currentState is ParkUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            Log.d("MY_TAG", "----> Pidiendo detalle del ID: $id")

            requestParkUseCase.execute(RequestParkUseCase.Parameters(id))
                .catch { e ->
                    Log.e("MY_TAG", "Error al cargar parque", e)
                    (uiState.value as? ParkUiState.Success)?.let {
                        _uiState.value = it.copy(isRefreshing = false)
                    }
                }
                .collect { result ->
                    val lastState = _uiState.value as? ParkUiState.Success
                    if (lastState != null) {
                        _uiState.value = lastState.copy(
                            selectedPark = result.data, // Aquí va el objeto Park
                            isRefreshing = false
                        )
                    }
                }
        }
    }
}