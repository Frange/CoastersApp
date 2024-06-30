package com.frange.coasters.data.repository.queue

import android.app.Application
import com.frange.coasters.data.api.park.toPark
import com.google.gson.Gson
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.data.api.parkinfo.response.ResponseParkList
import com.frange.coasters.data.api.parkinfo.response.toCompany
import com.frange.coasters.data.api.service.MockApiService
import com.frange.coasters.data.api.service.QueueApiService
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val application: Application,
    private val gson: Gson,
    private val service: QueueApiService,
    private val mockService: MockApiService
) : QueueRepository {

    companion object {
        const val COMPANY_PARQUES_REUNIDOS = "Parques Reunidos"
        const val PARK_WARNER = "Parque Warner Madrid"
    }

    private var companyList: List<Company> = arrayListOf()
    private var parkInfoList: List<ParkInfo> = arrayListOf()
    private var park: Park = Park(arrayListOf(), arrayListOf())
    private lateinit var rideList: List<Ride>

    private val isMock = true

    override fun requestCompanyList() = flow {
        emit(AppResult.loading())

        val response =
            if (isMock) mockService.requestMockCompanyList() else service.requestCompanyList()
        val formatedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
        companyList = searchAndSortCompany(formatedResponse.list?.map { it -> it.toCompany() }!!)

        emit(AppResult.success(companyList))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(position: Int) = flow {
        emit(AppResult.loading())

        parkInfoList = searchAndSortPark(companyList[position])

        emit(AppResult.success(parkInfoList))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestPark(position: Int, sortedByTime: Boolean) = flow {
        emit(AppResult.loading())

        val id = parkInfoList[position].id!!
        val response = service.requestPark(id)
        park = response.toPark()

        val rideList = mutableListOf<Ride>()
        if (!park.landList.isNullOrEmpty()) {
            park.landList!!.forEach {
                if (!it.rideList.isNullOrEmpty()) {
                    rideList.addAll(it.rideList)
                }
            }
        }
        park.rideList?.let { rideList.addAll(it) }

        val sortedList = if (sortedByTime) rideList.sortedBy { it.waitTime }
        else sortCoasterByStar(rideList)

        park.rideList = sortedList

        emit(AppResult.success(park))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestRideList() = flow {
        emit(AppResult.loading())

        rideList = park.rideList!!

        emit(AppResult.success(rideList))
    }

    override fun getCurrentCompanyList(): List<Company> {
        return companyList
    }

    override fun getCurrentParkList(): List<ParkInfo> {
        return parkInfoList
    }

    override fun getCurrentCoasterList(): Park {
        return park
    }

    private fun searchAndSortCompany(list: List<Company>): List<Company> {
        return if (list.isNotEmpty()) {
            val sortedList = list.sortedBy { it.name }
            val index = sortedList.indexOfFirst { it.name == COMPANY_PARQUES_REUNIDOS }
            if (index != -1) {
                mutableListOf(sortedList[index]).apply {
                    addAll(sortedList.filterNot { it.name == COMPANY_PARQUES_REUNIDOS })
                }
            } else {
                sortedList
            }
        } else {
            list
        }
    }

    private fun searchAndSortPark(
        company: Company
    ): List<ParkInfo> {
        val list = company.parks
        return if (!list.isNullOrEmpty()) {
            val sortedList = list.sortedBy { it.name }
            val index = sortedList.indexOfFirst { it.name == PARK_WARNER }
            if (index != -1) {
                mutableListOf(sortedList[index]).apply {
                    addAll(sortedList.filterNot { it.name == PARK_WARNER })
                }
            } else {
                sortedList
            }
        } else {
            arrayListOf()
        }
    }

    private fun sortCoasterByStar(rideList: List<Ride>?): List<Ride> {
        if (!rideList.isNullOrEmpty()) {
            val priorityList = listOf(
                "Batman Gotham City Escape",
                "BATMAN: Arkham Asylum",
                "SUPERMAN™: La Atracción de Acero",
                "Stunt Fall",
                "Coaster Express",
                "La Venganza del ENIGMA"
            )

            val withPriorityList = rideList.filter { it.name in priorityList }
            val withoutPriorityList = rideList.filter { it.name !in priorityList }

            return withPriorityList + withoutPriorityList
        } else {
            return arrayListOf()
        }
    }

}