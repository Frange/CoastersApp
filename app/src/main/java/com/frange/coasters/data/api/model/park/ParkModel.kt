package com.frange.coasters.data.api.model.park

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.Flow

interface ParkModel {

    fun get(id: Int): Flow<AppResult<List<Park>>>

}
