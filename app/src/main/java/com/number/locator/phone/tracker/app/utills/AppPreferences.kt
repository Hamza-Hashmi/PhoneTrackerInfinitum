package com.number.locator.phone.tracker.app.utills

import android.content.Context
import android.content.SharedPreferences
import com.phone.tracker.locate.number.app.utills.Constants


class AppPreferences(var context : Context) {

    fun putBoolean(key: String, value: Boolean) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(
                Constants.preferenceName,
                Context.MODE_PRIVATE
            )
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean( key: String, default: Boolean = false): Boolean {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(
                Constants.preferenceName,
                Context.MODE_PRIVATE
            )
        return sharedPref.getBoolean(key, default)
    }

    fun putString(key: String, value: String) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(
                Constants.preferenceName,
                Context.MODE_PRIVATE
            )
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString( key: String, default: String ): String {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(
                Constants.preferenceName,
                Context.MODE_PRIVATE
            )
        return sharedPref.getString(key, default)?:default
    }





}