package com.frange.coasters.data.repository.queue

import com.frange.coasters.data.api.park.toPark
import com.frange.coasters.data.api.parkinfo.response.ResponseParkList
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

    override fun requestParkList(id: Int) = flow {
        val response = service.requestPark(id)
        val apiPark = response.toPark()

        prefManager.userPreferencesFlow.collect { prefs ->
            val allRides = mutableListOf<Ride>()

            apiPark.landList?.forEach { land ->
                land.rideList?.let { allRides.addAll(it) }
            }
            apiPark.rideList?.let { allRides.addAll(it) }

            val uniqueRides = allRides.distinctBy { it.name }
            val sortedList = sortWithPreferences(uniqueRides, prefs)

            emit(AppResult.success(apiPark.copy(rideList = sortedList)))
        }
    }.onStart { emit(AppResult.loading()) }.flowOn(Dispatchers.IO)

    private fun sortWithPreferences(rideList: List<Ride>, prefs: UserPreferences): List<Ride> {
        return rideList.map { ride ->
            ride.copy(isFavourite = prefs.favoriteRideNames.contains(ride.name))
        }.sortedWith(
            compareByDescending<Ride> { it.isFavourite }
                .thenBy { !it.isOpen }
                .thenBy { it.waitTime }
                .thenBy { it.name }
        )
    }

    override fun requestAllParkList() = flow {
        try {
            val response = service.requestCompanyList()
            val formattedResponse = gson.fromJson("{list:$response}", ResponseParkList::class.java)
            val allParksBase = formattedResponse.list?.flatMap { it.parks ?: emptyList() } ?: emptyList()

            prefManager.userPreferencesFlow.collect { prefs ->
                val updatedList = allParksBase.map { park ->
                    park.copy(isFavourite = prefs.favoriteParkNames.contains(park.name))
                }.sortedWith(
                    compareByDescending<ParkInfo> { it.isFavourite }.thenBy { it.name }
                )
                emit(AppResult.success(updatedList))
            }
        } catch (e: Exception) {
            emit(AppResult.exception(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun requestParkInfoList(id: Int) = flow {
        val companyOwner = companyList.find { it.parks?.any { p -> p.id == id } == true }
        emit(AppResult.success(companyOwner?.parks?.sortedBy { it.name } ?: emptyList()))
    }

    override fun requestRideList() = flow {
        emit(AppResult.success(emptyList<Ride>()))
    }

    override suspend fun toggleRideFavorite(rideName: String) {
        prefManager.toggleRideFavorite(rideName)
    }

    override suspend fun toggleParkFavorite(parkName: String) {
        prefManager.toggleParkFavorite(parkName)
    }

    override fun getCurrentCompanyList() = companyList
    override fun getCurrentParkList() = parkInfoList
    override fun getCurrentCoasterList() = Park(null, null)
}