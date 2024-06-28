package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.parkinfo.model.ParkInfoModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCaseWithParams
import com.frange.coasters.domain.model.ParkInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestParkInfoListUseCase @Inject constructor(
    private val parkInfoModel: ParkInfoModel
) : FlowUseCaseWithParams<RequestParkInfoListUseCase.Parameters, AppResult<List<ParkInfo>>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<List<ParkInfo>>> {
        return parkInfoModel.get(parameters.id)
    }

    class Parameters(
        val id: Int
    )
}