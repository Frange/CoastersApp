package com.frange.coasters.domain.model


data class Ride(
    val id: Int?,
    val name: String?,
    val isOpen: Boolean,
    var isFavourite: Boolean = false,
    val waitTime: Int?,
    val lastUpdated: String?
)
