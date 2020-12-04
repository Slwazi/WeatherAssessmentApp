package com.example.lwazizwane.ui

import android.app.AlertDialog.Builder
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lwazizwane.R
import com.example.lwazizwane.api.getFavouriteJSON
import com.example.lwazizwane.customClasses.CustomList
import com.example.lwazizwane.classes.ForecastGrouping
import com.example.lwazizwane.sqllite.DB
import org.json.JSONObject
import java.util.*


class Settings : AppCompatActivity() {

    var add_favouritebtn: Button? = null
    var db: SQLiteDatabase? = null
    var helper: DB? = null
    var favourite_list: ArrayList<String>? = null
    var foreCastGroups: ArrayList<ForecastGrouping>? = null
    var selectedListview = ""
    var handler2: Handler? = null
    var getPrefs: SharedPreferences? = null
    var adapter: CustomList? = null
    var listView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)




        val actionBar = supportActionBar
        actionBar!!.hide()

        getPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)

        handler2 = Handler()

        add_favouritebtn = findViewById(R.id.searching)
        listView = findViewById<View>(R.id.list) as ListView
        helper = DB(this, null, null, 1)
        favourite_list = ArrayList<String>()
        foreCastGroups = ArrayList()







        val query = "Select * FROM  favourite"
        db = helper!!.writableDatabase

        val cursor = db!!.rawQuery(query, null)


        while (cursor.moveToNext()) {
            favourite_list!!.add(cursor.getString(1))

            updateWeatherForecast(cursor.getString(1))
        }


        Log.w("favourite_list",favourite_list.toString())



        listView!!.onItemClickListener = object : OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                //selectedListview = favourite_list!![position]
                val text = listView!!.getItemAtPosition(position).toString()
                val item = listView!!.adapter.getItem(position).toString()

                val forecastGroupings: ForecastGrouping = adapter?.getItem(position)!!


                createAndShowAlertDialog("Are you sure you want to delete ${forecastGroupings.date}?",forecastGroupings.date)



            }
        }





        add_favouritebtn!!.setOnClickListener {

            val intent = Intent(this@Settings, AddFavourite::class.java)
            startActivity(intent)

        }


    }


    private fun updateWeatherForecast( city_name:String) {
        object : Thread() {
            override fun run() {
                val json = getFavouriteJSON(applicationContext,city_name)
                if (json == null) {
                    handler2!!.post {
                        Toast.makeText(applicationContext,
                                applicationContext.getString(R.string.place_not_found),
                                Toast.LENGTH_LONG).show()
                    }
                } else {
                    handler2!!.post { renderWeather(json, city_name) }
                }
            }
        }.start()
    }

    private fun renderWeather(json: JSONObject, city_name:String) {
        try {



            val details = json.getJSONArray("weather").getJSONObject(0)
            val main = json.getJSONObject("main")
            val icon = details.getString("icon")







            if(icon == "01d"){
                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.oned,main.getInt("temp_max").toString() + " ℃"))


            } else if (icon == "01n") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.oned,main.getInt("temp_max").toString() + " ℃"))



            } else if (icon == "02d") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.oned,main.getInt("temp_max").toString() + " ℃"))


            } else if (icon == "02n") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.twon,main.getInt("temp_max").toString() + " ℃"))



            } else if (icon == "03d") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.threed,main.getInt("temp_max").toString() + " ℃"))



            } else if (icon == "03n") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.threen,main.getInt("temp_max").toString() + " ℃"))



            } else if (icon == "04d") {

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.fourd,main.getInt("temp_max").toString() + " ℃"))


            }else{

                foreCastGroups!!.add(ForecastGrouping(city_name,R.drawable.fourn,main.getInt("temp_max").toString() + " ℃"))

                // Log.w("temp_min", date.substring(date.indexOf(' ') + 1))

            }





            adapter = CustomList(foreCastGroups!!, applicationContext)
            listView!!.adapter = adapter


        } catch (e: Exception) {
            Log.e("Weather", "json_problem")
        }
    }

    private fun createAndShowAlertDialog(title:String,position: String) {
        val builder: Builder = Builder(this@Settings)
        builder.setTitle(title)
        builder.setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, id -> //TODO

            db = helper!!.writableDatabase
            db!!.delete("favourite", "city_name = ?", arrayOf<String>(position))


            finish();
            overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);

            dialog.dismiss()
        })
        builder.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, id -> //TODO
            dialog.dismiss()
        })
        val dialog: android.app.AlertDialog? = builder.create()
        dialog?.show()
    }

}