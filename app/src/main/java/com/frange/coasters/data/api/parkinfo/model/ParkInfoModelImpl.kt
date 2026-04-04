package com.frange.coasters.data.api.parkinfo.model

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.ParkInfo
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ParkInfoModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkInfoModel {

    override fun get(): Flow<AppResult<List<ParkInfo>>> {
        return repository.requestAllParkList().transform { result ->
            emit(result)
        }
    }

    override fun get(id: Int): Flow<AppResult<List<ParkInfo>>> {
        return repository.requestParkInfoList(id).transform { result ->
            emit(result)
        }
    }

    override suspend fun toggleFavorite(parkName: String) {
        repository.toggleParkFavorite(parkName)
    }
}