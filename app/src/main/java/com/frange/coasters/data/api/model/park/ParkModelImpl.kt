package com.frange.coasters.data.api.model.park

import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.Park
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ParkModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkModel {


//    override fun get(): Flow<AppResult<List<Company>>> {
//        return repository.requestCompanyList().transform { result ->
//            emit(result)
//        }
//    }
//
//    override fun get(id: Int): Flow<AppResult<List<Company>>> {
//        return flow {
//            val companyList = repository.getCurrentCompanyList()
//            emit(AppResult.success(companyList))
//        }
//    }

    override fun get(position: Int): Flow<AppResult<List<Park>>> {
        return repository.requestParkList(position).transform { result ->
            emit(result)
        }
//            repository.requestParkList(position)
//            val companyList = repository.getCurrentCompanyList()
//            if (companyList.isNotEmpty() && id <= companyList.size ){
//                emit(AppResult.success(companyList[id].parkList))
//            }
    }
}