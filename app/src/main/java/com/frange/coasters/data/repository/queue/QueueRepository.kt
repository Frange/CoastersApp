package com.frange.coasters.data.repository.queue

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Coaster
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun requestCompanyList(): Flow<AppResult<List<Company>>>
    fun requestParkList(position: Int): Flow<AppResult<List<Park>>>
    fun requestCoaster(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>>
    fun requestRideList(): Flow<AppResult<List<Ride>>>

    fun getCurrentCompanyList(): List<Company>
    fun getCurrentCoasterList(): Coaster
    fun getCurrentParkList(): List<Park>

}
