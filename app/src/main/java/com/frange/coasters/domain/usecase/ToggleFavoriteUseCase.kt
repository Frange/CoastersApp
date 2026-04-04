package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.park.ParkModel // O donde tengas la referencia al repo
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val parkModel: ParkModel
) {
    suspend operator fun invoke(rideName: String) {
        parkModel.toggleFavorite(rideName)
    }
}