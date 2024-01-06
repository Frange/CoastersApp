package com.frange.coasters.data.api.model.company

import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyModel {

    fun get(): Flow<AppResult<List<Company>>>
    fun get(id: Int): Flow<AppResult<List<Company>>>

}