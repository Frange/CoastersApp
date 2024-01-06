package com.frange.coasters.data.api.model.coaster.response.inner

import com.frange.coasters.domain.model.Ride
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ResponseRide(
    @Json(name = "id")
    var id: Int,

    @Json(name = "name")
    var name: String,

    @Json(name = "is_open")
    var is_open: Boolean,

    @Json(name = "wait_time")
    var wait_time: Int,

    @Json(name = "last_updated")
    var last_updated: String
)

fun ResponseRide.toRide() = Ride(
    id = id,
    name = name,
    isOpen = is_open,
    waitTime = wait_time,
    lastUpdated = last_updated,
)
