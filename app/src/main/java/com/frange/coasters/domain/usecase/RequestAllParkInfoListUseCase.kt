package com.frange.coasters.domain.usecase

import com.frange.coasters.data.api.parkinfo.model.ParkInfoModel
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.base.FlowUseCase
import com.frange.coasters.domain.model.ParkInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RequestAllParkInfoListUseCase @Inject constructor(
    private val parkInfoModel: ParkInfoModel
) : FlowUseCase<AppResult<List<ParkInfo>>>() {

    public override fun execute(): Flow<AppResult<List<ParkInfo>>> {
        return parkInfoModel.get()
    }

    class Parameters(
        val id: Int
    )
}