package com.frange.coasters.data.api.model.coaster

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Coaster
import kotlinx.coroutines.flow.Flow

interface CoasterModel {

    fun get(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>>

}
