package com.frange.coasters.domain.model

data class Park(
    val id: Int? = 0,
    val name: String,
    val country: String,
    val continent: String,
    val latitude: String,
    val longitude: String,
    val timezone: String
)
