package com.frange.coasters.data.api.service

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.InputStreamReader


class MockApiService(
    private val context: Context
) {

    companion object {
        const val MOCK_COMPANY_LIST_FILE = "mock_company_list.json"
    }

    fun requestMockCompanyList(): JsonArray {
        val assetManager = context.assets
        val inputStream = assetManager.open(MOCK_COMPANY_LIST_FILE)
        val jsonReader = InputStreamReader(inputStream)
        val jsonElement = JsonParser().parse(jsonReader)
        inputStream.close()

        return jsonElement.asJsonArray
    }

}