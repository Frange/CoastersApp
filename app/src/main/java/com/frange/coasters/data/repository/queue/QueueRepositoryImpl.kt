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
import com.frange.coasters.domain.model.Land
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
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

    private var companyList: List<Company> = arrayListOf()
    private var parkInfoList: List<ParkInfo> = arrayListOf()
    private var park: Park = Park(arrayListOf(), arrayListOf())
    private lateinit var rideList: List<Ride>
    private lateinit var landList: List<Land>

    private val isMock = false

    override fun requestAllParkList() = flow {
        emit(AppResult.loading())

        try {
            val response =
                if (isMock)
                    mockService.requestMockCompanyList()
                else
                    service.requestCompanyList()

            val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
            companyList = searchAndSortCompany(formattedResponse.list?.map { it.toCompany() }!!)

            val allParkInfo = mutableListOf<ParkInfo>()

            companyList.forEach { company ->
                val parkInfoList = searchAndSortPark(company)
                allParkInfo.addAll(parkInfoList)
            }

            parkInfoList = sortFavouriteParkInfoList(allParkInfo)

            emit(AppResult.success(parkInfoList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkInfoList(position: Int) = flow {
        emit(AppResult.loading())

        parkInfoList = searchAndSortPark(companyList[position])

        emit(AppResult.success(parkInfoList))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(position: Int, sortedByTime: Boolean) = flow {
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
            list.sortedBy { it.name }
        } else {
            list
        }
    }

    private fun sortFavouriteParkInfoList(parkInfoList: List<ParkInfo>): List<ParkInfo> {
        val priorityOrder = listOf(
            "Parque Warner Madrid",
            "Parque de Atracciones Madrid",
            "Europa Park",
            "Phantasialand"
        )

        val otherParks = parkInfoList.filter { it.name !in priorityOrder }
        val sortedPriorityParks = priorityOrder.mapNotNull { name ->
            parkInfoList.find { it.name == name }
        }
        return sortedPriorityParks + otherParks.sortedBy { it.name }
    }

    private fun searchAndSortPark(
        company: Company
    ): List<ParkInfo> {
        val list = company.parks
        return if (!list.isNullOrEmpty()) {
            list.sortedBy { it.name }
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
