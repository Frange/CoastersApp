package com.frange.coasters.data.api.model.ride

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.flow.Flow


interface RideModel {

    fun get(): Flow<AppResult<List<Ride>>>

}