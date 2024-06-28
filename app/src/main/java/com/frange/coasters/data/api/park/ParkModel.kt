package com.frange.coasters.data.api.park

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.Flow

interface ParkModel {

    fun get(position: Int, sortedByTime: Boolean): Flow<AppResult<Park>>

}
