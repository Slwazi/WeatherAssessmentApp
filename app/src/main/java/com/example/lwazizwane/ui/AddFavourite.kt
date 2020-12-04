package com.example.lwazizwane.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lwazizwane.R
import com.example.lwazizwane.api.geoName
import com.example.lwazizwane.classes.GeoName
import com.example.lwazizwane.customClasses.GeoNameCustomList
import com.example.lwazizwane.sqllite.DB
import org.json.JSONObject
import java.util.*


class AddFavourite : AppCompatActivity() {

    var handler2: Handler? = null
    var geoNameModels: ArrayList<GeoName>? = null

    var adapter: GeoNameCustomList? = null
    var autoCompleteTextView: AutoCompleteTextView? =null
    var arrayList:ArrayList<String>? = null
    var add_favourite: Button? = null
    var cancel: Button? = null
    var getautoCompleteTextView: String = ""
    var getPrefs: SharedPreferences? = null

    var db: SQLiteDatabase? = null
    var helper: DB? = null
    var add_favourite_city: String? = null
    var GeoNamesPref = ""
    var UploadedPref = ""

    var city_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_favourite)


        val actionBar = supportActionBar
        actionBar!!.hide()

        handler2 = Handler()
        helper = DB(this, null, null, 1)

        add_favourite = findViewById<View>(R.id.add_favourite) as Button
        cancel = findViewById<View>(R.id.cancel) as Button
        autoCompleteTextView = findViewById<View>(R.id.autoComplete) as AutoCompleteTextView


        getGeoNames()


        getPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)


        add_favourite!!.setOnClickListener {


            getautoCompleteTextView = autoCompleteTextView!!.text.toString()


            val countQuery = "SELECT  * FROM favourite WHERE city_name  ='$getautoCompleteTextView'"
            db = helper!!.readableDatabase

            val c = db!!.rawQuery(countQuery, null)
            if (c.moveToFirst()) {
                Toast.makeText(this, "City already added to favourite", Toast.LENGTH_LONG).show()
            } else {



                for (d in geoNameModels!!) {
                    if (d.name == getautoCompleteTextView) {


                        helper!!.addCity(d.name,d.lat,d.lng)


                    }
                }

                db!!.close()

                Toast.makeText(this, "City Added to Favourite", Toast.LENGTH_LONG).show()


                val handler = Handler()
                handler.postDelayed({ // Do something after 5s = 5000ms
                    val packageManager: PackageManager = applicationContext.packageManager
                    val intent = packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                    val componentName = intent!!.component
                    val mainIntent = Intent.makeRestartActivityTask(componentName)
                    applicationContext.startActivity(mainIntent)
                    Runtime.getRuntime().exit(0)
                }, 5000)



            }


        }


        cancel!!.setOnClickListener {


            autoCompleteTextView!!.setText("")


           onBackPressed()

        }


    }




    private fun getGeoNames() {
        object : Thread() {
            override fun run() {
                val json = geoName(applicationContext)


                if (json == null) {
                    handler2!!.post {


                        GeoNamesPref = getPrefs!!.getString("GeoNames", null)!!


                        if ((GeoNamesPref == null) || (GeoNamesPref.isEmpty())) {

                            Toast.makeText(applicationContext,getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show()

                        }else{

                            val obj = JSONObject(GeoNamesPref)

                            handler2!!.post { renderGeoNames(obj) }
                        }


                    }
                } else {

                    val editor: SharedPreferences.Editor = getPrefs!!.edit()
                    editor.putString("GeoNames", json.toString())
                    editor.putString("Uploades", "1")
                    editor.commit()


                    GeoNamesPref = getPrefs!!.getString("GeoNames", null)!!


                    if ((GeoNamesPref == null) || (GeoNamesPref.isEmpty())) {


                    }else{

                        val obj = JSONObject(GeoNamesPref)

                        handler2!!.post { renderGeoNames(obj) }
                    }

                }
            }
        }.start()
    }

    private fun renderGeoNames(json: JSONObject) {
        try {

            val geo_Name = JSONObject(json.toString())
            val geo_Name_list = geo_Name.getJSONArray("geonames")

            val geoName_list = json.getJSONArray("geonames").getJSONObject(0)

            var name: String
            var toponymName: String
            var lat: Double
            var lng: Double
            var fcode: String


            geoNameModels = ArrayList()
            arrayList = ArrayList()

            for (i in 0 until geo_Name_list.length()) {

                val daily_forecast = geo_Name_list.getJSONObject(i)
                name = daily_forecast.getString("name")
                toponymName = daily_forecast.getString("toponymName")
                lat = daily_forecast.getDouble("lat")
                lng = daily_forecast.getDouble("lng")
                fcode = daily_forecast.getString("fcode")


                geoNameModels!!.add(GeoName(name,toponymName,lat,lng,fcode))
                arrayList!!.add(name)

            }


            val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_dropdown_item_1line, arrayList!! as List<Any?>)



            adapter = GeoNameCustomList(geoNameModels!!, applicationContext)
            autoCompleteTextView!!.setAdapter(arrayAdapter)
            autoCompleteTextView!!.threshold = 1



            autoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {

                }
            })


        } catch (e: Exception) {
            Log.e("Weather", "json_problem")
        }
    }


}