package com.example.lwazizwane.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lwazizwane.R
import com.example.lwazizwane.api.getForecastJSON
import com.example.lwazizwane.api.getJSON
import com.example.lwazizwane.customClasses.CustomList
import com.example.lwazizwane.classes.ForecastGrouping
import com.example.lwazizwane.classes.ForecastModel
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CurrentLocationWeather : Fragment(), LocationListener {


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
    var lastUpdated: TextView? = null

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

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    var location: Location? = null
    var provider: String? = null


    var getPrefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_one, container, false)



        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        //gety location permission on run
        getLocationPermissionOnRun()





        getPrefs = PreferenceManager.getDefaultSharedPreferences(activity!!.baseContext)




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
        lastUpdated = view.findViewById(R.id.lastUpdate)
        current_city = view.findViewById(R.id.current_city)
        temp_min = view.findViewById(R.id.temp_min)
        temp_max = view.findViewById(R.id.temp_max)
        listView = view.findViewById<View>(R.id.list) as ListView
        bottom_layout = view.findViewById(R.id.bottom_layout)



        lastUpdated?.setVisibility(View.INVISIBLE)



        return view

    }

    private fun updateWeatherData() {
        object : Thread() {
            override fun run() {
                val json = activity?.let { getJSON(it, _lat, _lan) }



                if (json == null) {
                    handler!!.post {

                        lastUpdated?.setVisibility(View.VISIBLE)

                        lastUpdated!!.setOnClickListener {


                            activity?.finish();
                            startActivity(activity?.getIntent())
                            activity?.overridePendingTransition(0, 0);
                        }


                        CurrentWeatherPref = getPrefs!!.getString("CurrentWeather", null)!!
                        lastUpdateTime = getPrefs!!.getString("CurrentTime", null)!!
                        lastUpdated?.setText("Last Updated: $lastUpdateTime")


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

                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    val editor: SharedPreferences.Editor = getPrefs!!.edit()
                    editor.putString("CurrentWeather", json.toString())
                    editor.putString("CurrentTime", currentDateTimeString)
                    editor.commit()




                    CurrentWeatherPref = getPrefs!!.getString("CurrentWeather", null)!!


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
            current_weather!!.text = main.getInt("feels_like").toString() + " ℃"
            temp_current!!.text = main.getInt("feels_like").toString() + " ℃"
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


                        var _SP_INSPECTION = ""

                        _SP_INSPECTION = getPrefs!!.getString("JSONValue", null)!!


                        if ((_SP_INSPECTION == null) || (_SP_INSPECTION.isEmpty())) {


                            Toast.makeText(activity,
                                    activity?.getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show()


                        }else{

                            val obj = JSONObject(_SP_INSPECTION)

                            handler!!.post { forecast(obj) }
                        }


                    }
                } else {

                    val editor: SharedPreferences.Editor = getPrefs!!.edit()
                    editor.putString("JSONValue", json.toString())
                    editor.commit()

                    var _SP_INSPECTION = ""

                    _SP_INSPECTION = getPrefs!!.getString("JSONValue", null)!!


                    if ((_SP_INSPECTION == null) || (_SP_INSPECTION.isEmpty())) {



                    }else{

                        val obj = JSONObject(_SP_INSPECTION)

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
                val weather_forecast_clouds = daily_forecast.getJSONObject("clouds")
                val weather_forecast_sys = daily_forecast.getJSONObject("sys")
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

            // display only weather at 12 to list
            for (d in forecastDataModels!!) {
                if (d.time == "12:00:00") {


                    foreCastGroups!!.add(ForecastGrouping(d.date,d.images, d.temp_Max))

                    //Log.w("images", d.images.toString())




                }
            }


            adapter = activity?.let { CustomList(foreCastGroups!!, it) }
            listView!!.adapter = adapter




        } catch (e: Exception) {
           // Log.e("SimpleWeather", "One or more fields not found in the JSON data")
        }
    }
    private fun getLocationPermissionOnRun() {


        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 30) }
        }



        try {
            locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            provider = LocationManager.GPS_PROVIDER
            if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            }
            //locationManager.requestLocationUpdates(provider, 35000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

            if (location != null) {

                onLocationChanged(location!!)
            } else {

            }
        } catch (e: java.lang.Exception) {
            /*e.printStackTrace();*/

        }
    }
    override fun onLocationChanged(location: Location) {

        _lat = location.latitude
        _lan = location.longitude



        updateWeatherData()
        updateWeatherForecast()


        progressdialog?.dismiss()



        try {

            getLocationAddress()

        }catch (e: Exception) {

        }




        //tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {


    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")


    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) //Log.d("RCODEEEEEE",Integer.toString(requestCode));
        when (requestCode) {
            30 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0]!!)) {
                //Toast.makeText(MainActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

    }

    override fun onPause() {
        super.onPause()
        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED && activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.removeUpdates(this as LocationListener)
    }

    private fun getLocationAddress(){


        val geocoder: Geocoder
        var addresses: List<Address?>
        geocoder = Geocoder(activity, Locale.getDefault())


        try {

            addresses = geocoder.getFromLocation(_lat, _lan, 1)
            if (addresses != null && addresses.size > 0) {
                val address = addresses[0]!!.getAddressLine(0)
                val city = addresses[0]!!.locality
                val state = addresses[0]!!.adminArea
                val country = addresses[0]!!.countryName
                val postalCode = addresses[0]!!.postalCode
                val knownName = addresses[0]!!.featureName



                current_city!!.text = city


            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }


    }




    companion object {
        fun newInstance(): CurrentLocationWeather {
            return CurrentLocationWeather()
        }
    }
}
