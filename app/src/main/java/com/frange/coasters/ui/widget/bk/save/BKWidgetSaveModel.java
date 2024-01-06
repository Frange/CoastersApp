package com.frange.coasters.ui.widget.bk.save;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.frange.coasters.domain.model.Ride;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BKWidgetSaveModel {

    private static final String TAG = "MY_WIDGET";

    private static final String PREF_KEY_JSON = "jsonData";
    private static final String JSON_EMPTY = "[]";

    private static final String PARSER_FIELD_ID = "id";
    private static final String PARSER_FIELD_NAME = "name";
    private static final String PARSER_FIELD_IS_OPEN = "is_open";
    private static final String PARSER_FIELD_WAIT_TIME = "wait_time";
    private static final String PARSER_FIELD_LAST_UPDATE = "last_update";
    private static final String PARSER_FIELD_FIRST_ITEM = "firstitem";

    public static void saveData(Context context, List<Ride> list) {
        SharedPreferences sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        String json = "[]";

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);

            Log.v(TAG, "saveData - json: " + json);

            if (list != null && !list.isEmpty()) {
                for (Ride ride : list) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(PARSER_FIELD_ID, ride.getId());
                    jsonObject.put(PARSER_FIELD_NAME, ride.getName());
                    jsonObject.put(PARSER_FIELD_IS_OPEN, ride.isOpen());
                    jsonObject.put(PARSER_FIELD_WAIT_TIME, ride.getWaitTime());
                    jsonObject.put(PARSER_FIELD_LAST_UPDATE, ride.getLastUpdated());
                    jsonArray.put(jsonObject);
                }
            }
            sharedPref.edit().putString(PREF_KEY_JSON, jsonArray.toString()).apply();

        } catch (JSONException e) {
            Log.v(TAG, "saveData - exception: " + e);
        }
    }

    public static ArrayList<Ride> loadData(Context context) {
        ArrayList<Ride> list = new ArrayList<>();
        if (list.isEmpty()) {
            SharedPreferences sharedPref = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
            String json = sharedPref.getString(PREF_KEY_JSON, JSON_EMPTY);

            Log.v(TAG, "loadData - json: " + json);

            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Ride model = new Ride(
                            object.getInt(PARSER_FIELD_ID),
                            object.getString(PARSER_FIELD_NAME),
                            object.getBoolean(PARSER_FIELD_IS_OPEN),
                            object.getInt(PARSER_FIELD_WAIT_TIME),
                            object.getString(PARSER_FIELD_LAST_UPDATE));

//                    model.setFirstItem(object.has(PARSER_FIELD_FIRST_ITEM) &&
//                            object.getBoolean(PARSER_FIELD_FIRST_ITEM));

                    list.add(model);
                }
            } catch (JSONException e) {
                Log.v(TAG, "getDataFromSharedPrefs - exception: " + e);
            }
        }

        return list;
    }

}
