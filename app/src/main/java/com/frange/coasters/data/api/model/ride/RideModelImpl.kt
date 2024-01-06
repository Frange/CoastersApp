package com.frange.coasters.data.api.model.ride

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject


class RideModelImpl @Inject constructor(
    private val repository: QueueRepository
) : RideModel {

    override fun get(): Flow<AppResult<List<Ride>>> {
        return repository.requestRideList().transform { result ->
            emit(result)
        }
    }

}