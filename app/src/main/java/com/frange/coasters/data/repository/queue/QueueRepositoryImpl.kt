package com.frange.coasters.data.repository.queue

import android.app.Application
import com.frange.coasters.utils.priorityList
import com.frange.coasters.utils.priorityOrder
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
    private val gson: Gson,
    private val service: QueueApiService,
    private val mockService: MockApiService
) : QueueRepository {

    private var companyList: List<Company> = emptyList()
    private var parkInfoList: List<ParkInfo> = emptyList()
    private var park: Park = Park(mutableListOf(), mutableListOf())

    private val isMock = false

    override fun requestAllParkList() = flow {
        emit(AppResult.loading())
        try {
            val response = if (isMock) mockService.requestMockCompanyList() else service.requestCompanyList()

            val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
            companyList = searchAndSortCompany(formattedResponse.list?.map { it.toCompany() } ?: emptyList())

            val allParkInfo = mutableListOf<ParkInfo>()
            companyList.forEach { company ->
                val parks = searchAndSortPark(company)
                allParkInfo.addAll(parks)
            }

            parkInfoList = sortFavouriteParkInfoList(allParkInfo)

            emit(AppResult.success(parkInfoList))
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkInfoList(id: Int) = flow {
        emit(AppResult.loading())

        val companyOwner = companyList.find { company ->
            company.parks?.any { it.id == id } == true
        }

        val list = if (companyOwner != null) {
            searchAndSortPark(companyOwner)
        } else {
            emptyList()
        }

        emit(AppResult.success(list))
    }.catch {
        emit(AppResult.exception(it))
    }.flowOn(Dispatchers.IO)

    override fun requestParkList(id: Int) = flow {
        emit(AppResult.loading())

        val response = service.requestPark(id)
        park = response.toPark()

        // 1. Extraer todas las atracciones (de tierras y de la lista principal)
        val allRides = mutableListOf<Ride>()
        park.landList?.forEach { land ->
            land.rideList?.let { allRides.addAll(it) }
        }
        park.rideList?.let { allRides.addAll(it) }

        // 2. Aplicar orden de FAVORITOS (priorityList)
        val sortedList = sortFavouriteRides(allRides)
        park.rideList = sortedList

        emit(AppResult.success(park))
    }.catch {
        emit(AppResult.exception(it))
    }.flowOn(Dispatchers.IO)

    override fun requestRideList() = flow {
        emit(AppResult.loading())
        emit(AppResult.success(park.rideList ?: emptyList()))
    }

    private fun searchAndSortCompany(list: List<Company>): List<Company> =
        list.sortedBy { it.name }

    private fun sortFavouriteParkInfoList(list: List<ParkInfo>): List<ParkInfo> {
        val (priority, others) = list.partition { it.name in priorityOrder }
        val sortedPriority = priorityOrder.mapNotNull { name -> priority.find { it.name == name } }
        return sortedPriority + others.sortedBy { it.name }
    }

    private fun searchAndSortPark(company: Company): List<ParkInfo> =
        company.parks?.sortedBy { it.name } ?: emptyList()

    private fun sortFavouriteRides(rideList: List<Ride>?): List<Ride> {
        if (rideList.isNullOrEmpty()) return emptyList()

        val sortedRides = mutableListOf<Ride>()

        priorityList.forEach { favName ->
            rideList.find { it.name!!.uppercase() == favName.uppercase() }?.let {
                it.isFavourite = true
                sortedRides.add(it)
            }
        }

        val rest = rideList.filter { it.name !in priorityList }
        sortedRides.addAll(rest)

        return sortedRides
    }

    override fun getCurrentCompanyList() = companyList
    override fun getCurrentParkList() = parkInfoList
    override fun getCurrentCoasterList() = park
}