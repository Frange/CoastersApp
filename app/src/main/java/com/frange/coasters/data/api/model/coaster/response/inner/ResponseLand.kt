package com.frange.coasters.data.api.model.coaster.response.inner

import com.frange.coasters.domain.model.Land
import com.frange.coasters.domain.model.Ride
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseLand(
    @Json(name = "id")
    var id: Int,

    @Json(name = "name")
    var name: String,

    @Json(name = "rides")
    var rides: List<Ride>

)

fun ResponseLand.toLand() = Land(
    id = id,
    name = name,
    rideList = rides,
)
