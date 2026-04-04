package com.frange.coasters.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.frange.coasters.domain.model.UserPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_settings")
@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val KEY_FAVORITES = stringPreferencesKey("json_favorites")

    // Tus listas antiguas ahora son el "Default"
    private val defaultRides = setOf(
        "Batman Gotham City Escape", "Shadows of Arkham", "SUPERMAN La Atracción de Acero",
        "Stunt Fall", "Coaster Express", "La Venganza del ENIGMA", "Hotel Embrujado",
        "CORRECAMINOS Bip Bip", "TOM & JERRY PICNIC EN EL PARQUE", "MR. FREEZE FÁBRICA DE HIELO",
        "OSO YOGUI", "Cataratas Salvajes", "Rápidos ACME", "Río Bravo", "Abismo", "Tarántula",
        "La Máquina", "Tornado", "Lanzadera", "Top Spin", "Vértigo", "Star Flyer",
        "TNT - Tren de la Mina", "Tifón", "Aserradero", "Los Fiordos", "Shambhala",
        "Dragon Khan", "Furius Baco", "Hurakan Condor", "Tutuki Splash", "Grand Canyon Rapids",
        "Stampida", "El Diablo - Tren De La Mina", "Uncharted", "Templo del Fuego", "Red Force",
        "Thrill Towers", "Racing Legends", "Flying Dreams", "Maranello Grand Race", "Taron",
        "Black Mamba", "Talocan", "Crazy Bats", "Maus au Chocolat", "Chiapas - DIE Wasserbahn",
        "Mystery Castle", "River Quest", "F.L.Y.", "Silver Star", "blue fire Megacoaster",
        "WODAN - Timburcoaster", "Eurosat - CanCan Coaster", "Voltron Nevera powered by Rimac",
        "ARTHUR", "Water rollercoaster Poseidon", "Eurosat Coastiality", "Euro-Mir",
        "Alpine Express 'Enzian'", "Josefina’s Magical Imperial Journey", "Voletarium",
        "Pegasus", "Pirates in Batavia", "Fjord-Rafting", "Star Trek™: Operation Enterprise",
        "Van Helsing’s Factory", "The Lost Temple", "High Fall Tower",
        "Excalibur - Secrets of the Dark Forest", "Backyardigans Mission to Mars", "The Bandit",
        "NYC Transformer", "Crazy Surfer", "Area 51 - Top Secret", "Zadra Rc", "Hyperion Rc",
        "Abyssus", "Moya Formula Rc", "Mayan Rc", "Speed Rc", "Rmf Dragon Rc",
        "Tidal Wave Twister", "Boomerang ", "Anaconda", "Ekipa Light Explorers", "Frida Rc",
        "Vikingløp", "Vinter Rytt", "Svalgur Rytt", "Vildstrøm", "Stormvind", "Dugdrob",
        "Vildfål", "Tønnevirvel", "Isbrekker", "Snorri's Saga"
    )

    private val defaultParks = setOf(
        "Parque Warner Madrid", "Parque de Atracciones Madrid", "PortAventura Park",
        "Ferrari Land", "Europa Park", "Rulantica", "Phantasialand",
        "Movie Park Germany", "Energylandia"
    )

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_FAVORITES] ?: ""
        if (json.isEmpty()) {
            // Si es la primera vez, devolvemos los defaults
            UserPreferences(favoriteParkNames = defaultParks, favoriteRideNames = defaultRides)
        } else {
            gson.fromJson(json, UserPreferences::class.java)
        }
    }

    suspend fun toggleRideFavorite(rideName: String) {
        updatePrefs { it.copy(favoriteRideNames = toggleItem(it.favoriteRideNames, rideName)) }
    }

    suspend fun toggleParkFavorite(parkName: String) {
        updatePrefs { it.copy(favoriteParkNames = toggleItem(it.favoriteParkNames, parkName)) }
    }

    suspend fun saveLastParkId(id: Int) {
        updatePrefs { it.copy(lastSelectedParkId = id) }
    }

    private suspend fun updatePrefs(transform: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_FAVORITES] ?: ""
            val current = if (json.isEmpty()) UserPreferences(defaultParks, defaultRides)
            else try { gson.fromJson(json, UserPreferences::class.java) } catch(e: Exception) { UserPreferences(defaultParks, defaultRides) }

            prefs[KEY_FAVORITES] = gson.toJson(transform(current))
        }
    }

    private fun toggleItem(set: Set<String>, item: String) = if (set.contains(item)) set - item else set + item
}
