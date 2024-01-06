package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.model.park.ParkModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCaseWithParams
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestParkListUseCase @Inject constructor(
    private val parkModel: ParkModel
) : FlowUseCaseWithParams<RequestParkListUseCase.Parameters, AppResult<List<Park>>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<List<Park>>> {
        return parkModel.get(parameters.id)
    }

    class Parameters(
        val id: Int
    )
}