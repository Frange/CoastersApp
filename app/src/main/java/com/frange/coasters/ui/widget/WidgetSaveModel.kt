package com.frange.coasters.ui.widget

import android.content.Context
import android.util.Log
import com.frange.coasters.domain.model.Ride
import org.json.JSONArray
import org.json.JSONObject

class WidgetSaveModel {
    companion object {
        private const val PREF_KEY_JSON = "widget_rides_json"

        fun saveData(context: Context, list: List<Ride>?) {
            val sharedPref = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            val jsonArray = JSONArray()
            try {
                list?.forEach { ride ->
                    val jsonObject = JSONObject().apply {
                        put("id", ride.id ?: 0)
                        put("name", ride.name ?: "")
                        put("is_open", ride.isOpen)
                        put("wait_time", ride.waitTime)
                        put("is_favourite", ride.isFavourite)
                    }
                    jsonArray.put(jsonObject)
                }
                sharedPref.edit().putString(PREF_KEY_JSON, jsonArray.toString()).apply()
            } catch (e: Exception) {
                Log.e("WIDGET_SAVE", "Error saving: $e")
            }
        }

        fun loadData(context: Context): List<Ride> {
            val list = mutableListOf<Ride>()
            val sharedPref = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            val json = sharedPref.getString(PREF_KEY_JSON, "[]") ?: "[]"
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(Ride(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        isOpen = obj.getBoolean("is_open"),
                        isFavourite = obj.optBoolean("is_favourite", false),
                        waitTime = obj.getInt("wait_time"),
                        lastUpdated = ""
                    ))
                }
            } catch (e: Exception) {
                Log.e("WIDGET_LOAD", "Error loading: $e")
            }
            return list
        }
    }
}