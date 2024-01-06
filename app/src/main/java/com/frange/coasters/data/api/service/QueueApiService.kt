package com.frange.coasters.data.api.service

import com.google.gson.JsonArray
import com.frange.coasters.data.api.model.coaster.response.inner.ResponseCoasterData
import retrofit2.http.GET
import retrofit2.http.Path

interface QueueApiService {

    @GET("parks.json")
    suspend fun requestCompanyList(): JsonArray

    @GET("parks/{id}/queue_times.json")
    suspend fun requestCoasters(
        @Path("id") parkId: Int = 298
    ): ResponseCoasterData

}