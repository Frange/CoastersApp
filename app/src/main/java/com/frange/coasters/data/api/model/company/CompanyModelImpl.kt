package com.frange.coasters.data.api.model.company

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Company
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class CompanyModelImpl @Inject constructor(
    private val repository: QueueRepository
) : CompanyModel {

    override fun get(): Flow<AppResult<List<Company>>> {
        return repository.requestCompanyList().transform { result ->
            emit(result)
        }
    }

    override fun get(id: Int): Flow<AppResult<List<Company>>> {
        return flow {
            val companyList = repository.getCurrentCompanyList()
            emit(AppResult.success(companyList))
        }
    }
}