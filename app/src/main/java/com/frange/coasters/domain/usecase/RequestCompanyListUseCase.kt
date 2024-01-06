package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.model.company.CompanyModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCase
import com.frange.coasters.domain.model.Company
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestCompanyListUseCase @Inject constructor(
    private val companyModel: CompanyModel
) : FlowUseCase<AppResult<List<Company>>>() {

    public override fun execute(): Flow<AppResult<List<Company>>> {
        return companyModel.get()
    }
}