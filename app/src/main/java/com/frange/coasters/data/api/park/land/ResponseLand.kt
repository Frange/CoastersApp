package com.frange.coasters.data.api.park.land

import com.frange.coasters.data.api.park.ride.response.ResponseRide
import com.frange.coasters.data.api.park.ride.response.toRide
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
    var rides: List<ResponseRide>

)

fun ResponseLand.toLand() = Land(
    id = id,
    name = name,
    rideList = rides.map { it.toRide() }
)
