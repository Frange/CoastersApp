package com.frange.coasters.data.api.service

import com.google.gson.JsonArray
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InfoCaptainApiService {

    @GET("parks.json")
    suspend fun requestParks(
        @Query("page") page: Int = 1
    ): JsonArray

    @GET("https://queue-times.com/parks/{id}/queue_times.json")
    suspend fun requestCoasters(
        @Path("id") parkId: Int = 1
    ): JsonArray

}
