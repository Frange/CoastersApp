package com.frange.coasters.data.api.parkinfo.model

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.ParkInfo
import kotlinx.coroutines.flow.Flow

interface ParkInfoModel {
    fun get(): Flow<AppResult<List<ParkInfo>>>
    fun get(id: Int): Flow<AppResult<List<ParkInfo>>>
    suspend fun toggleFavorite(parkName: String)
}
