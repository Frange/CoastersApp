package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.park.ParkModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCaseWithParams
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestParkUseCase @Inject constructor(
    private val parkModel: ParkModel
) : FlowUseCaseWithParams<RequestParkUseCase.Parameters, AppResult<Park>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<Park>> {
        return parkModel.get(parameters.id)
    }

    class Parameters(
        val id: Int
    )
}