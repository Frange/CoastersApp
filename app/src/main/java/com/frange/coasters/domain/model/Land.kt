package com.frange.coasters.domain.model

data class Land(
    val id: Int,
    val name: String,
    val rideList: List<Ride>?
)