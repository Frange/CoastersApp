package com.frange.coasters.ui.widget

import android.content.Context
import android.util.Log
import com.frange.coasters.domain.model.Ride
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class WidgetSaveModel {

    companion object {
        private const val TAG = "MY_WIDGET"
        private const val PREF_KEY_JSON = "jsonData"
        private const val JSON_EMPTY = "[]"
        private const val PARSER_FIELD_ID = "id"
        private const val PARSER_FIELD_NAME = "name"
        private const val PARSER_FIELD_IS_OPEN = "is_open"
        private const val PARSER_FIELD_WAIT_TIME = "wait_time"
        private const val PARSER_FIELD_LAST_UPDATE = "last_update"
        private const val PARSER_FIELD_FIRST_ITEM = "firstitem"

        fun saveData(context: Context, list: List<Ride>?) {
            val sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
            var json = "[]"

            try {
                val jsonArray = JSONArray(json)

                Log.v(TAG, "saveData - json: $json")

                list?.let {
                    if (it.isNotEmpty()) {
                        for (ride in it) {
                            val jsonObject = JSONObject().apply {
                                put(PARSER_FIELD_ID, ride.id)
                                put(PARSER_FIELD_NAME, ride.name)
                                put(PARSER_FIELD_IS_OPEN, ride.isOpen)
                                put(PARSER_FIELD_WAIT_TIME, ride.waitTime)
                                put(PARSER_FIELD_LAST_UPDATE, ride.lastUpdated)
                            }
                            jsonArray.put(jsonObject)
                        }
                    }
                }
                sharedPref.edit().putString(PREF_KEY_JSON, jsonArray.toString()).apply()

            } catch (e: JSONException) {
                Log.v(TAG, "saveData - exception: $e")
            }
        }

        fun loadData(context: Context): ArrayList<Ride> {
            val list = ArrayList<Ride>()

            if (list.isEmpty()) {
                val sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
                val json = sharedPref.getString(PREF_KEY_JSON, JSON_EMPTY)

                Log.v(TAG, "loadData - json: $json")

                try {
                    val jsonArray = JSONArray(json)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val model = Ride(
                            jsonObject.getInt(PARSER_FIELD_ID),
                            jsonObject.getString(PARSER_FIELD_NAME),
                            jsonObject.getBoolean(PARSER_FIELD_IS_OPEN),
                            jsonObject.getInt(PARSER_FIELD_WAIT_TIME),
                            jsonObject.getString(PARSER_FIELD_LAST_UPDATE)
                        )

                        // model.setFirstItem(jsonObject.has(PARSER_FIELD_FIRST_ITEM) &&
                        //         jsonObject.getBoolean(PARSER_FIELD_FIRST_ITEM))

                        list.add(model)
                    }
                } catch (e: JSONException) {
                    Log.v(TAG, "getDataFromSharedPrefs - exception: $e")
                }
            }

            return list
        }
    }

}
