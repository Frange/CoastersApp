package com.frange.coasters.data.repository.queue

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun requestAllParkList(): Flow<AppResult<List<ParkInfo>>>

    fun requestParkInfoList(id: Int): Flow<AppResult<List<ParkInfo>>>

    fun requestParkList(id: Int): Flow<AppResult<Park>>

    fun requestRideList(): Flow<AppResult<List<Ride>>>

    suspend fun toggleParkFavorite(parkName: String)

    fun getCurrentCompanyList(): List<Company>
    fun getCurrentCoasterList(): Park
    fun getCurrentParkList(): List<ParkInfo>
}