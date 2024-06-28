package com.frange.coasters.data.api.company.response

import com.frange.coasters.data.api.park.ParkResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParkListResponse(
    @Json(name = "list")
    var list: List<ParkResponse>?
)