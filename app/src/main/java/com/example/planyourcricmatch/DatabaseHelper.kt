package com.example.planyourcricmatch

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Matches.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "matches"
        const val COL_ID = "id"
        const val COL_MATCH_NAME = "match_name"
        const val COL_TEAM = "team"
        const val COL_AGAINST_TEAM = "against_team"
        const val COL_DATE = "date"
        const val COL_LOCATION = "location"
        const val COL_FORMAT = "format"
        const val COL_STADIUM = "stadium"
        const val COL_SLOT = "slot"
        const val COL_WEATHER = "weather"
        const val COL_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_MATCH_NAME TEXT, $COL_TEAM TEXT, $COL_AGAINST_TEAM TEXT," +
                " $COL_DATE TEXT, $COL_LOCATION TEXT, $COL_FORMAT TEXT, $COL_STADIUM TEXT, $COL_SLOT TEXT, $COL_WEATHER TEXT, $COL_STATUS TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
