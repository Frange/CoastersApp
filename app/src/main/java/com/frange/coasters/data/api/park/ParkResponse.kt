package com.frange.coasters.data.api.park

import com.frange.coasters.data.api.park.land.ResponseLand
import com.frange.coasters.data.api.park.land.toLand
import com.frange.coasters.data.api.park.ride.response.ResponseRide
import com.frange.coasters.data.api.park.ride.response.toRide
import com.frange.coasters.domain.model.Park
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParkResponse(
    @Json(name = "rides")
    var rides: List<ResponseRide>,

    @Json(name = "lands")
    var lands: List<ResponseLand>,
)

fun ParkResponse.toPark() = Park(
    rideList = rides.map { it.toRide() },
    landList = lands.map { it.toLand() }
)
