package com.frange.coasters.data.api.park

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ParkModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkModel {

    override fun get(id: Int): Flow<AppResult<Park>> {
        return repository.requestParkList(id).transform { result ->
            emit(result)
        }
    }

}