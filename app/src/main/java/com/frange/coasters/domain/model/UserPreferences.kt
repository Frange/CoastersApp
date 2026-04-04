package com.frange.coasters.domain.model

import androidx.annotation.Keep

@Keep
data class UserPreferences(
    val favoriteParkNames: Set<String> = emptySet(),
    val favoriteRideNames: Set<String> = emptySet()
)