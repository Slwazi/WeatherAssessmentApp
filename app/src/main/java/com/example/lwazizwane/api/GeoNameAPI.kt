package com.example.lwazizwane.api

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val GEONAME_API = "http://api.geonames.org/searchJSON?username=slwazi&country=ZA&style=SHORT"

fun geoName(context: Context): JSONObject? {
    return try {
        val url = URL(GEONAME_API)
        val connection = url.openConnection() as HttpURLConnection

        val reader = BufferedReader(
                InputStreamReader(connection.inputStream))
        val json = StringBuffer(102895)
        var tmp: String? = ""
        while (reader.readLine().also { tmp = it } != null) json.append(tmp).append("\n")
        reader.close()
        val data = JSONObject(json.toString())

        if (data.getInt("totalResultsCount") != 102895) {
            null
        } else data
    } catch (e: Exception) {
        null
    }
}