package com.frange.coasters.data.api.service

import com.frange.coasters.data.api.park.ParkResponse
import com.google.gson.JsonArray
import retrofit2.http.GET
import retrofit2.http.Path

interface QueueApiService {

    @GET("parks.json")
    suspend fun requestCompanyList(): JsonArray

    @GET("parks/{id}/queue_times.json")
    suspend fun requestPark(
        @Path("id") parkId: Int = 298
    ): ParkResponse

}