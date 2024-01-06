package com.frange.coasters.data.repository.queue

import android.app.Application
import com.google.gson.Gson
import com.frange.coasters.data.api.model.coaster.response.inner.toCoaster
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.data.api.model.park.response.ResponseParkList
import com.frange.coasters.data.api.model.park.response.toCompany
import com.frange.coasters.data.api.service.QueueApiService
import com.frange.coasters.domain.model.Coaster
import com.frange.coasters.domain.model.Company
import com.frange.coasters.domain.model.Park
import com.frange.coasters.domain.model.Ride
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val application: Application,
    private val gson: Gson,
    private val service: QueueApiService
) : QueueRepository {

    companion object {
        const val COMPANY_PARQUES_REUNIDOS = "Parques Reunidos"
        const val PARK_WARNER = "Parque Warner Madrid"
    }

    private var companyList: List<Company> = arrayListOf()
    private var parkList: List<Park> = arrayListOf()
    private var coaster: Coaster = Coaster(arrayListOf(), arrayListOf())
    private lateinit var rideList: List<Ride>

    override fun requestCompanyList() = flow {
        emit(AppResult.loading())

        val response = service.requestCompanyList()
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

        parkList = searchAndSortPark(companyList[position].parkList!!)

        emit(AppResult.success(parkList))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestCoaster(position: Int, sortedByTime: Boolean) = flow {
        emit(AppResult.loading())

        val id = parkList[position].id!!
        val response = service.requestCoasters(id)
        coaster = response.toCoaster()
        val cleanedRideList = cleanNameList(coaster.rideList)

        val sortedList = if (sortedByTime) coaster.rideList?.sortedBy { it.waitTime }
        else sortCoasterByStar(coaster.rideList)

        coaster.rideList = sortedList

        emit(AppResult.success(coaster))
    }.catch {
        emit(
            AppResult.exception(it)
        )
    }.flowOn(Dispatchers.IO)

    override fun requestRideList() = flow {
        emit(AppResult.loading())

        rideList = coaster.rideList!!

        emit(AppResult.success(rideList))
    }

    override fun getCurrentCompanyList(): List<Company> {
        return companyList
    }

    override fun getCurrentParkList(): List<Park> {
        return parkList
    }

    override fun getCurrentCoasterList(): Coaster {
        return coaster
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

    private fun searchAndSortPark(list: List<Park>): List<Park> {
        return if (list.isNotEmpty()) {
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
            list
        }
    }

    private fun cleanNameList(rideList: List<Ride>?): List<Ride>? {
        rideList?.forEach { cleanName(it.name) }

        return rideList
    }

    private fun cleanName(name: String?): String? {
        return when (name) {
            "Batman Gotham City Escape" -> "Gotham City Escape"
            "BATMAN: Arkham Asylum" -> "Arkham Asylum"
            "SUPERMAN™: La Atracción de Acero" -> "Superman"
            "Coaster Express" -> "Coaster Express (madera)"

            else -> name
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