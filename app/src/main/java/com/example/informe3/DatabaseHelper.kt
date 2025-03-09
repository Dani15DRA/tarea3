package com.example.informe3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_USERS: String = "users"
        const val COLUMN_ID: String = "id"
        const val COLUMN_USERNAME: String = "username"
        const val COLUMN_PASSWORD: String = "password"
        const val COLUMN_EMAIL: String = "email"
        const val COLUMN_FULL_NAME: String = "full_name"

        private const val SQL_CREATE_USERS_TABLE = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT UNIQUE NOT NULL, " +
                "$COLUMN_PASSWORD TEXT NOT NULL, " +
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_FULL_NAME TEXT)"
    }
}