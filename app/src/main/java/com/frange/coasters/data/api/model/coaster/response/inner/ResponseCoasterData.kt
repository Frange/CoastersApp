package com.frange.coasters.data.api.model.coaster.response.inner

import com.frange.coasters.domain.model.Coaster
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseCoasterData(
    @Json(name = "rides")
    var rides: List<ResponseRide>,

    @Json(name = "lands")
    var lands: List<ResponseLand>,
)

fun ResponseCoasterData.toCoaster() = Coaster(
    rideList = rides.map { it.toRide() },
    landList = lands.map { it.toLand() }
)
