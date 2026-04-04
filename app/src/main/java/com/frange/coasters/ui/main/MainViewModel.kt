package com.frange.coasters.ui.main

import androidx.lifecycle.*
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.Status
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import com.frange.coasters.domain.usecase.RequestAllParkInfoListUseCase
import com.frange.coasters.domain.usecase.RequestParkUseCase
import com.frange.coasters.domain.usecase.ToggleFavoriteUseCase
import com.frange.coasters.domain.usecase.ToggleParkFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestCompanyListUseCase: RequestAllParkInfoListUseCase,
    private val requestParkUseCase: RequestParkUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleParkFavoriteUseCase: ToggleParkFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ParkUiState>(ParkUiState.Loading)
    val uiState: StateFlow<ParkUiState> = _uiState.asStateFlow()

    private var parksJob: kotlinx.coroutines.Job? = null
    private var ridesJob: kotlinx.coroutines.Job? = null
    private var isFirstTime = true

    init {
        observeAllParks()
    }

    fun observeAllParks() {
        parksJob?.cancel()
        parksJob = viewModelScope.launch {
            requestCompanyListUseCase.execute()
                .catch { e ->
                    _uiState.value = ParkUiState.Error(e.message ?: "Error de conexión")
                }
                .collect { result ->
                    // Comparación directa con el status de tu AppResult
                    if (result.status == Status.SUCCESS) {
                        val rawData = result.data as? List<Any?> ?: emptyList()
                        val allParks = mutableListOf<ParkInfo>()

                        rawData.forEach { item ->
                            when (item) {
                                is Company -> item.parks?.let { allParks.addAll(it) }
                                is ParkInfo -> allParks.add(item)
                            }
                        }

                        val cleanParks = allParks.filter { it.id != null }.distinctBy { it.id }

                        _uiState.update { currentState ->
                            if (currentState is ParkUiState.Success) {
                                currentState.copy(availableParks = cleanParks)
                            } else {
                                ParkUiState.Success(
                                    availableParks = cleanParks,
                                    selectedPark = null,
                                    isRefreshing = false
                                )
                            }
                        }

                        if (isFirstTime && cleanParks.isNotEmpty()) {
                            isFirstTime = false
                            cleanParks.first().id?.let { requestPark(it) }
                        }
                    }
                }
        }
    }

    fun requestAllParkInfoList() {
        observeAllParks()
    }

    fun requestPark(parkId: Int) {
        val currentState = _uiState.value as? ParkUiState.Success ?: return
        _uiState.value = currentState.copy(isRefreshing = true)

        ridesJob?.cancel()
        ridesJob = viewModelScope.launch {
            requestParkUseCase.execute(RequestParkUseCase.Parameters(parkId))
                .catch { _ ->
                    _uiState.update { (it as? ParkUiState.Success)?.copy(isRefreshing = false) ?: it }
                }
                .collect { result ->
                    // Comparación directa con Status.SUCCESS
                    if (result.status == Status.SUCCESS) {
                        _uiState.update { state ->
                            if (state is ParkUiState.Success) {
                                state.copy(
                                    selectedPark = result.data,
                                    isRefreshing = false
                                )
                            } else state
                        }
                    }
                }
        }
    }

    fun toggleFavorite(ride: Ride) {
        val name = ride.name ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(name)
        }
    }

    fun toggleParkFavorite(parkName: String) {
        viewModelScope.launch {
            toggleParkFavoriteUseCase(parkName)
        }
    }
}