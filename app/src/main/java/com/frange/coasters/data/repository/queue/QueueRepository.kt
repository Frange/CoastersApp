package com.frange.coasters.data.repository.queue

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun requestCompanyList(): Flow<AppResult<List<Company>>>
    fun requestParkList(position: Int): Flow<AppResult<List<ParkInfo>>>
    fun requestPark(position: Int, sortedByTime: Boolean): Flow<AppResult<Park>>
    fun requestRideList(): Flow<AppResult<List<Ride>>>

    fun getCurrentCompanyList(): List<Company>
    fun getCurrentCoasterList(): Park
    fun getCurrentParkList(): List<ParkInfo>

}
