package com.frange.coasters.data.api.park

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParkListResponse(
    @Json(name = "list")
    var list: List<ParkResponse>?
)