package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.park.ParkModel
import javax.inject.Inject

class ToggleRideFavoriteUseCase @Inject constructor(
    private val parkModel: ParkModel
) {
    suspend operator fun invoke(rideName: String) {
        parkModel.toggleRideFavorite(rideName)
    }
}