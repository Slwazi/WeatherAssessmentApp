package com.example.lwazizwane.api

import android.content.Context
import com.example.lwazizwane.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?"
var temp: String? = ""

fun getFavouriteJSON(context: Context, city_name: String): JSONObject? {
    return try {
        val url = URL(OPEN_WEATHER_MAP_API + "q="+ city_name + ",rsa"  + "&units=metric")
        val connection = url.openConnection() as HttpURLConnection
        connection.addRequestProperty("x-api-key",
                context.getString(R.string.open_weather_maps_app_id))
        val reader = BufferedReader(
                InputStreamReader(connection.inputStream))
        val json = StringBuffer(1024)

        while (reader.readLine().also { temp = it } != null) json.append(temp).append("\n")
        reader.close()
        val data = JSONObject(json.toString())

        // This value will be 404 if the request was not
        // successful
        if (data.getInt("cod") != 200) {
            null
        } else data
    } catch (e: Exception) {
        null
    }
}