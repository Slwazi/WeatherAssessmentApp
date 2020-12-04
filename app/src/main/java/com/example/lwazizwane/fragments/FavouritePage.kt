package com.example.lwazizwane.fragments


import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lwazizwane.R
import com.example.lwazizwane.api.getForecastJSON
import com.example.lwazizwane.api.getJSON
import com.example.lwazizwane.customClasses.CustomList
import com.example.lwazizwane.classes.ForecastGrouping
import com.example.lwazizwane.classes.ForecastModel
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FavouritePage : Fragment(){


    var relativeLayout: RelativeLayout? = null
    var bottom_layout : RelativeLayout? = null

    var handler: Handler? = null
    var handler2: Handler? = null
    var current_weather: TextView? = null
    var description: TextView? = null
    var temp_min: TextView? = null
    var temp_max: TextView? = null
    var temp_current: TextView? = null
    var current_city: TextView? = null
    var lastUpdatedTextView: TextView? = null

    var temp_list: ArrayList<String>? = null
    var curentkey = ""
    var forecastDataModels: ArrayList<ForecastModel>? = null
    var foreCastGroups: ArrayList<ForecastGrouping>? = null
    var listView: ListView? = null
    var adapter: CustomList? = null
    var progressdialog: ProgressDialog? = null


    var _lat = 0.0
    var _lan = 0.0
    var CurrentWeatherPref = ""
    var lastUpdateTime =""
    var cityName =""




    var getPrefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_one, container, false)



        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        getPrefs = PreferenceManager.getDefaultSharedPreferences(activity!!.baseContext)


        //retrieve cityname,lat,lon from WeatherMain Activity
        val args = arguments
        _lat = args!!.getDouble("lat", 0.0)
        _lan = args!!.getDouble("lon", 0.0)
        cityName = args!!.getString("cityName", "")





        progressdialog = ProgressDialog(activity)
        progressdialog!!.setMessage("Please Wait....")

        progressdialog!!.show()

        handler = Handler()
        handler2 = Handler()
        temp_list = ArrayList()

        relativeLayout = view.findViewById(R.id.main)
        current_weather = view.findViewById(R.id.current_weather)
        description = view.findViewById(R.id.description)
        temp_current = view.findViewById(R.id.temp_current)
        lastUpdatedTextView = view.findViewById(R.id.lastUpdate)
        current_city = view.findViewById(R.id.current_city)
        temp_min = view.findViewById(R.id.temp_min)
        temp_max = view.findViewById(R.id.temp_max)
        listView = view.findViewById<View>(R.id.list) as ListView
        bottom_layout = view.findViewById(R.id.bottom_layout)



        // hide textview when internet available

        lastUpdatedTextView?.visibility = View.INVISIBLE




        current_city!!.text = cityName


        updateWeatherData()
        updateWeatherForecast()


        progressdialog?.dismiss()




        return view

    }

    private fun updateWeatherData() {
        object : Thread() {
            override fun run() {
                val json = activity?.let { getJSON(it, _lat, _lan) }



                if (json == null) {
                    handler!!.post {

                        lastUpdatedTextView?.visibility = View.VISIBLE

                        lastUpdatedTextView!!.setOnClickListener {


                            activity?.finish()
                            startActivity(activity?.intent)
                            activity?.overridePendingTransition(0, 0)
                        }


                        CurrentWeatherPref = getPrefs!!.getString("CurrentWeather"+cityName, null)!!
                        lastUpdateTime = getPrefs!!.getString("CurrentTime"+cityName, null)!!
                        lastUpdatedTextView?.text = "Last Updated: $lastUpdateTime"


                        if ((CurrentWeatherPref == null) || (CurrentWeatherPref.isEmpty())) {

                            Toast.makeText(activity,
                                    activity?.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show()

                        }else{

                            val obj = JSONObject(CurrentWeatherPref)

                            handler!!.post { renderWeather(obj) }
                        }



                    }
                } else {

                    //store json in preference to read last updated
                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    val editor: SharedPreferences.Editor = getPrefs!!.edit()
                    editor.putString("CurrentWeather"+cityName, json.toString())
                    editor.putString("CurrentTime"+cityName, currentDateTimeString)
                    editor.commit()




                    CurrentWeatherPref = getPrefs!!.getString("CurrentWeather"+cityName, null)!!


                    if ((CurrentWeatherPref == null) || (CurrentWeatherPref.isEmpty())) {



                    }else{

                        val obj = JSONObject(CurrentWeatherPref)

                        handler!!.post { renderWeather(obj) }
                    }

                }
            }
        }.start()
    }

    private fun renderWeather(json: JSONObject) {
        try {



            val details = json.getJSONArray("weather").getJSONObject(0)
            val main = json.getJSONObject("main")
            description!!.text = details.getString("description")
            current_weather!!.text = main.getInt("temp").toString() + " ℃"
            temp_current!!.text = main.getInt("temp").toString() + " ℃"
            temp_max!!.text = main.getInt("temp_max").toString() + " ℃"
            temp_min!!.text = main.getInt("temp_min").toString() + " ℃"




            if (details.getString("description") == "thunderstorm with light rain") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_rainy)
                activity?.let { ContextCompat.getColor(it, R.color.rainy) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "light rain") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_rainy)
                activity?.let { ContextCompat.getColor(it, R.color.rainy) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "few clouds") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_cloudy)
                activity?.let { ContextCompat.getColor(it, R.color.cloudy) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "clear sky") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_sunny)
                activity?.let { ContextCompat.getColor(it, R.color.sunny) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "scattered clouds") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_cloudy)
                activity?.let { ContextCompat.getColor(it, R.color.cloudy) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "broken clouds") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_cloudy)
                activity?.let { ContextCompat.getColor(it, R.color.cloudy) }?.let { bottom_layout!!.setBackgroundColor(it) }


            } else if (details.getString("description") == "overcast clouds") {

                relativeLayout!!.setBackgroundResource(R.drawable.forest_cloudy)
                activity?.let { ContextCompat.getColor(it, R.color.cloudy) }?.let { bottom_layout!!.setBackgroundColor(it) }

            }



        } catch (e: Exception) {
            Log.e("Weather", "json_problem")
        }
    }
    private fun updateWeatherForecast() {
        object : Thread() {
            override fun run() {
                val json = activity?.let { getForecastJSON(it, _lat, _lan) }


                if (json == null) {
                    handler2!!.post {


                        var pref_json_array = ""

                        pref_json_array = getPrefs!!.getString("JSONValue"+cityName, null)!!


                        if ((pref_json_array == null) || (pref_json_array.isEmpty())) {


                            Toast.makeText(activity,
                                    activity?.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show()


                        }else{

                            val obj = JSONObject(pref_json_array)

                            handler!!.post { forecast(obj) }
                        }


                    }
                } else {

                    val editor: SharedPreferences.Editor = getPrefs!!.edit()
                    editor.putString("JSONValue"+cityName, json.toString())
                    editor.commit()

                    var pref_Json = ""

                    pref_Json = getPrefs!!.getString("JSONValue"+cityName, null)!!


                    if ((pref_Json == null) || (pref_Json.isEmpty())) {



                    }else{

                        val obj = JSONObject(pref_Json)

                        handler!!.post { forecast(obj) }
                    }




                }
            }
        }.start()
    }

    private fun forecast(json: JSONObject) {
        try {
            val weather_forecast = JSONObject(json.toString())
            val weather_forecast_list = weather_forecast.getJSONArray("list")
            var temp_min: String
            var temp_max: String
            var date: String
            var description = ""
            var icon = ""


            forecastDataModels = ArrayList()
            foreCastGroups = ArrayList()

            //Log.w("weather_", String.valueOf(details));
            for (i in 0 until weather_forecast_list.length()) {
                val daily_forecast = weather_forecast_list.getJSONObject(i)
                val weather_forecast_main = daily_forecast.getJSONObject("main")
                val weather_forecast_weather_list = daily_forecast.getJSONArray("weather")
                //val weather_forecast_clouds = daily_forecast.getJSONObject("clouds")
                //val weather_forecast_sys = daily_forecast.getJSONObject("sys")
                //Log.w("weather_forecast_clouds", weather_forecast_sys.toString())

                for (x in 0 until weather_forecast_weather_list.length()) {
                    val weather_forecast_weather_ = weather_forecast_weather_list.getJSONObject(x)
                    description = weather_forecast_weather_.getString("description")
                    icon = weather_forecast_weather_.getString("icon")
                    //  Log.w("weather_", description)
                }
                //temp_min = weather_forecast_main.getString("temp_min")
                temp_max = weather_forecast_main.getInt("temp_max").toString()
                date = daily_forecast.getString("dt_txt")
                val tk = StringTokenizer(date)
                val date_ = tk.nextToken()
                val format1 = SimpleDateFormat("yyyy-MM-dd")
                val dt1 = format1.parse(date_)
                val format2: DateFormat = SimpleDateFormat("EEEE")
                val finalDay = format2.format(dt1)


                // Log.w("icon", icon)


                if(icon == "01d"){

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.oned))
                    // Log.w("icon", icon)

                } else if (icon == "01n") {

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.onen))
                    // Log.w("icon", icon)

                } else if (icon == "02d") {


                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.twod))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

                } else if (icon == "02n") {

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.twon))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

                } else if (icon == "03d") {

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.threed))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

                } else if (icon == "03n") {

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.threen))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))


                } else if (icon == "04d") {

                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.fourd))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

                }else{


                    forecastDataModels!!.add(ForecastModel(finalDay, date.substring(date.indexOf(' ') + 1), icon, temp_max+ " ℃",R.drawable.fourn))
                    // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

                }



            }


            //get weather at 12:00

            for (weatherList in forecastDataModels!!) {
                if (weatherList.time == "12:00:00") {


                    foreCastGroups!!.add(ForecastGrouping(weatherList.date,weatherList.images, weatherList.temp_Max))



                }
            }


            adapter = activity?.let { CustomList(foreCastGroups!!, it) }
            listView!!.adapter = adapter




        } catch (e: Exception) {
             Log.e("`error`", "Json not set well")
        }
    }



    companion object {
        fun newInstance(cityname:String,lat: Double, lon: Double): FavouritePage {


            val f = FavouritePage()
            val args = Bundle()
            args.putString("cityName", cityname)
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            f.arguments = args


            return f
        }
    }
}