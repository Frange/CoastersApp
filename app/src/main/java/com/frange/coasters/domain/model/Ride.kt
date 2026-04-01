package com.frange.coasters.domain.model


data class Ride(
    val id: Int?,
    val name: String?,
    var isOpen: Boolean = false,
    var isFavourite: Boolean = false,
    val waitTime: Int = 0,
    val lastUpdated: String?
)
