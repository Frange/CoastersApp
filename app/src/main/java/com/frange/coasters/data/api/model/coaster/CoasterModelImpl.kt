package com.frange.coasters.data.api.model.coaster

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Coaster
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CoasterModelImpl @Inject constructor(
    private val repository: QueueRepository
) : CoasterModel {

    override fun get(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>> {
        return repository.requestCoaster(position, sortedByTime).transform { result ->
            emit(result)
        }
    }

}