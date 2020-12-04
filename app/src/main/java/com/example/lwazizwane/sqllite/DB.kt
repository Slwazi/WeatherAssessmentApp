package com.example.lwazizwane.sqllite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.example.lwazizwane.classes.GeoName
import java.util.*

class DB(context: Context?, name: String?,
         factory: CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    var product: ArrayList<GeoName>? = null
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_FAVOURITE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME
                + " TEXT," + COLUMN_LAT + " REAL," + COLUMN_LON + " REAL"
                + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVOURITE")
        onCreate(db)
    }

    fun addCity(cityname: String?, lat: Double?, lon: Double?) {
        val values = ContentValues()
        values.put(COLUMN_NAME, cityname)
        values.put(COLUMN_LAT, lat)
        values.put(COLUMN_LON, lon)
        val db = this.writableDatabase
        db.insert(TABLE_FAVOURITE, null, values)
        db.close()
    }

    // product = new Product(Integer.parseInt(cursor.getString(0),cursor.getString(1))
    // cursor.close();

    // return product();
    val data: Unit
        get() {
            val query = "Select * FROM $TABLE_FAVOURITE"
            val db = this.writableDatabase
            val cursor = db.rawQuery(query, null)
            product = ArrayList()
            while (cursor.moveToNext()) {
                product!!.add(GeoName(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getString(4)))

                // product = new Product(Integer.parseInt(cursor.getString(0),cursor.getString(1))
            }
            // cursor.close();

            // return product();
        }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "favourite.db"
        private const val TABLE_FAVOURITE = "favourite"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "city_name"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LON = "lon"
    }
}