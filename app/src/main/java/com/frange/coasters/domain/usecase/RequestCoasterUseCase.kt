package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.model.coaster.CoasterModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCaseWithParams
import com.frange.coasters.domain.model.Coaster
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestCoasterUseCase @Inject constructor(
    private val coasterModel: CoasterModel
) : FlowUseCaseWithParams<RequestCoasterUseCase.Parameters, AppResult<Coaster>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<Coaster>> {
        return coasterModel.get(parameters.id, parameters.sortedByTime)
    }

    class Parameters(
        val id: Int,
        val sortedByTime: Boolean
    )
}