package com.frange.coasters.data.repository.queue

import com.frange.coasters.data.api.park.toPark
import com.frange.coasters.data.api.parkinfo.response.ResponseParkList
import com.frange.coasters.data.api.parkinfo.response.toCompany
import com.frange.coasters.data.api.service.MockApiService
import com.frange.coasters.data.api.service.QueueApiService
import com.frange.coasters.data.store.PreferenceManager
import com.frange.coasters.domain.base.AppResult
import com.frange.coasters.domain.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val gson: Gson,
    private val service: QueueApiService,
    private val mockService: MockApiService,
    private val prefManager: PreferenceManager
) : QueueRepository {

    private var companyList: List<Company> = emptyList()
    private var parkInfoList: List<ParkInfo> = emptyList()
    private var currentPark: Park = Park(mutableListOf(), mutableListOf())

    override fun requestParkList(id: Int) = flow {
        val response = service.requestPark(id)
        currentPark = response.toPark()

        prefManager.userPreferencesFlow.collect { prefs ->
            val allRides = mutableListOf<Ride>()
            currentPark.landList?.forEach { land -> land.rideList?.let { allRides.addAll(it) } }
            currentPark.rideList?.let { allRides.addAll(it) }

            val sortedList = sortWithPreferences(allRides, prefs)
            currentPark.rideList = sortedList

            emit(AppResult.success(currentPark))
        }
    }.onStart { emit(AppResult.loading()) }.flowOn(Dispatchers.IO)

    private fun sortWithPreferences(rideList: List<Ride>, prefs: UserPreferences): List<Ride> {
        val (favs, others) = rideList.partition { ride ->
            prefs.favoriteRideNames.contains(ride.name)
        }

        val sortedFavs = favs.onEach { it.isFavourite = true }
            .sortedWith(compareBy<Ride> { !it.isOpen }.thenBy { it.waitTime })

        val sortedOthers = others.onEach { it.isFavourite = false }
            .sortedBy { it.name }

        return sortedFavs + sortedOthers
    }

    override fun requestParkInfoList(id: Int) = flow {
        val companyOwner = companyList.find { it.parks?.any { p -> p.id == id } == true }
        emit(AppResult.success(companyOwner?.parks?.sortedBy { it.name } ?: emptyList()))
    }

    override fun requestRideList() = flow {
        emit(AppResult.success(currentPark.rideList ?: emptyList()))
    }

    override fun requestAllParkList() = flow {
        // 1. Obtenemos la lista base de la API una vez
        val response = service.requestCompanyList()
        val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
        val allParksBase = formattedResponse.list?.flatMap { it.parks ?: emptyList() } ?: emptyList()

        // 2. Nos suscribimos a los cambios de favoritos (DataStore)
        prefManager.userPreferencesFlow.collect { prefs ->
            val updatedList = allParksBase.map { park ->
                park.copy(isFavourite = prefs.favoriteParkNames.contains(park.name))
            }.sortedWith(
                compareByDescending<ParkInfo> { it.isFavourite }
                    .thenBy { it.name }
            )

            emit(AppResult.success(updatedList))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun toggleParkFavorite(parkName: String) {
        prefManager.toggleParkFavorite(parkName)
    }

    override fun getCurrentCompanyList() = companyList
    override fun getCurrentParkList() = parkInfoList
    override fun getCurrentCoasterList() = currentPark
}