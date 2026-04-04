package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.parkinfo.model.ParkInfoModel
import javax.inject.Inject

class ToggleParkFavoriteUseCase @Inject constructor(
    private val parkInfoModel: ParkInfoModel
) {
    suspend operator fun invoke(parkName: String) {
        parkInfoModel.toggleFavorite(parkName)
    }
}