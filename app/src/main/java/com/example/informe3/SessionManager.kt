package com.example.informe3

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "UserSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_EMAIL = "email"
    }

    fun createLoginSession(user: User) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_FULL_NAME, user.fullName)
        editor.putString(KEY_EMAIL, user.email)
        // Do not store the password in SharedPreferences

        // Commit changes
        editor.apply()
    }

    fun getUserDetails(): HashMap<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails[KEY_USER_ID] = sharedPreferences.getLong(KEY_USER_ID, -1).toString()
        userDetails[KEY_USERNAME] = sharedPreferences.getString(KEY_USERNAME, null) ?: ""
        userDetails[KEY_FULL_NAME] = sharedPreferences.getString(KEY_FULL_NAME, null) ?: ""
        userDetails[KEY_EMAIL] = sharedPreferences.getString(KEY_EMAIL, null) ?: ""

        return userDetails
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logoutUser() {
        // Clear all data from SharedPreferences
        editor.clear()
        editor.apply()
    }
}