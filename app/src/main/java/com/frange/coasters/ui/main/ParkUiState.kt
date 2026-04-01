package com.frange.coasters.ui.main

import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.ParkInfo

sealed class ParkUiState {
    object Loading : ParkUiState()

    data class Success(
        val availableParks: List<ParkInfo> = emptyList(),
        val selectedPark: Park? = null,
        val isRefreshing: Boolean = false
    ) : ParkUiState()

    data class Error(val message: String) : ParkUiState()
}