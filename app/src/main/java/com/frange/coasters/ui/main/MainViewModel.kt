package com.frange.coasters.ui.main

import androidx.lifecycle.*
import com.frange.coasters.domain.base.Status
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import com.frange.coasters.domain.usecase.RequestAllParkInfoListUseCase
import com.frange.coasters.domain.usecase.RequestParkUseCase
import com.frange.coasters.domain.usecase.ToggleRideFavoriteUseCase
import com.frange.coasters.domain.usecase.ToggleParkFavoriteUseCase
import com.frange.coasters.data.store.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val requestCompanyListUseCase: RequestAllParkInfoListUseCase,
    private val requestParkUseCase: RequestParkUseCase,
    private val toggleRideFavoriteUseCase: ToggleRideFavoriteUseCase,
    private val toggleParkFavoriteUseCase: ToggleParkFavoriteUseCase,
    private val prefManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ParkUiState>(ParkUiState.Loading)
    val uiState: StateFlow<ParkUiState> = _uiState.asStateFlow()

    private var parksJob: kotlinx.coroutines.Job? = null
    private var ridesJob: kotlinx.coroutines.Job? = null

    private var currentLoadedParkId: Int? = null
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
                    if (result.status == Status.SUCCESS) {
                        val rawData = result.data as? List<Any?> ?: emptyList()
                        val allParks = rawData.flatMap { item ->
                            when (item) {
                                is Company -> item.parks ?: emptyList()
                                is ParkInfo -> listOf(item)
                                else -> emptyList()
                            }
                        }.filter { it.id != null }.distinctBy { it.id }

                        _uiState.update { currentState ->
                            if (currentState is ParkUiState.Success) {
                                currentState.copy(availableParks = allParks)
                            } else {
                                ParkUiState.Success(
                                    availableParks = allParks,
                                    selectedPark = null,
                                    selectedParkId = 0,
                                    isRefreshing = false
                                )
                            }
                        }

                        // Lógica de arranque: Solo ocurre UNA VEZ
                        if (isFirstTime && allParks.isNotEmpty()) {
                            isFirstTime = false
                            val savedId = prefManager.userPreferencesFlow.first().lastSelectedParkId
                            // Si el ID guardado no existe en la lista actual, usamos el primero
                            val targetId = savedId?.takeIf { id -> allParks.any { it.id == id } }
                                ?: allParks.first().id

                            targetId?.let { requestPark(it) }
                        }
                    }
                }
        }
    }

    fun requestAllParkInfoList() {
        observeAllParks()
    }

    fun requestPark(parkId: Int) {
        // Persistencia inmediata
        viewModelScope.launch {
            prefManager.saveLastParkId(parkId)
        }

        // Si ya estamos cargando este parque, no reiniciamos el flujo para no perder la reactividad
        if (currentLoadedParkId == parkId && ridesJob?.isActive == true) {
            // Si entramos aquí es un pull-to-refresh manual, activamos el círculo de carga
            _uiState.update { if (it is ParkUiState.Success) it.copy(isRefreshing = true) else it }
        }

        currentLoadedParkId = parkId

        _uiState.update { state ->
            if (state is ParkUiState.Success) {
                state.copy(selectedParkId = parkId, isRefreshing = true)
            } else state
        }

        ridesJob?.cancel()
        ridesJob = viewModelScope.launch {
            requestParkUseCase.execute(RequestParkUseCase.Parameters(parkId))
                .catch { _ ->
                    _uiState.update { (it as? ParkUiState.Success)?.copy(isRefreshing = false) ?: it }
                }
                .collect { result ->
                    if (result.status == Status.SUCCESS) {
                        _uiState.update { state ->
                            if (state is ParkUiState.Success) {
                                state.copy(selectedPark = result.data, isRefreshing = false)
                            } else state
                        }
                    }
                }
        }
    }

    fun toggleRideFavorite(ride: Ride) {
        viewModelScope.launch {
            ride.name?.let { toggleRideFavoriteUseCase(it) }
        }
    }

    fun toggleParkFavorite(parkName: String) {
        viewModelScope.launch {
            toggleParkFavoriteUseCase(parkName)
        }
    }
}