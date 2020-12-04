package com.example.lwazizwane.ui

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.lwazizwane.R
import com.example.lwazizwane.customClasses.TabAdaptor
import com.example.lwazizwane.fragments.CurrentLocationWeather
import com.example.lwazizwane.fragments.FavouritePage
import com.example.lwazizwane.sqllite.DB
import com.google.android.material.tabs.TabLayout

class WeatherMain : AppCompatActivity() {


    var mTabs: TabLayout? = null
    var mIndicator: View? = null
    var mViewPager: ViewPager? = null
    var settings: TextView? = null
    var db: SQLiteDatabase? = null
    var helper: DB? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_main)




        val actionBar = supportActionBar
        actionBar!!.hide()

        helper = DB(this, null, null, 1)



        mTabs = findViewById(R.id.tab_layout)
        mViewPager = findViewById(R.id.photos_viewpager)
        settings = findViewById(R.id.settings)


        val adapter = TabAdaptor(supportFragmentManager)
        adapter.addFragment(CurrentLocationWeather.newInstance(), "")



        val query = "Select * FROM  favourite"
        db = helper!!.writableDatabase


        val cursor = db!!.rawQuery(query, null)


        while (cursor.moveToNext()) {
            adapter.addFragment(FavouritePage.newInstance(cursor.getString(1),cursor.getDouble(2),cursor.getDouble(3)), "")

        }



        mViewPager?.setAdapter(adapter)
        mTabs?.setupWithViewPager(mViewPager)





        settings!!.setOnClickListener {


            val intent = Intent(this@WeatherMain, Settings::class.java)
            startActivity(intent)
        }

        
    }




}