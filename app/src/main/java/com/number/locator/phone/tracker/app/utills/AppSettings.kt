package com.phone.tracker.locate.number.app.utills

import android.content.Context
import android.content.SharedPreferences
import com.number.locator.phone.tracker.app.utills.edit


class AppSettings(context: Context) {


    private  val Prefereces = "preference"
    private val PreferecesService = "service"
    private  val PreferecesSpeedUnit = "speed_unit"
    private  val PreferecesUpdatingScreen = "keep_updating_while_screen_off"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(Prefereces, Context.MODE_PRIVATE)

    var isRunning: Boolean
        get() = preferences.getBoolean(PreferecesService, false)
        set(running) = preferences.edit {
            putBoolean(PreferecesService, running)
        }

    var units: StandardSpeedUnits
        get() = StandardSpeedUnits.valueOf(preferences.getString(PreferecesSpeedUnit, StandardSpeedUnits.KILOMETERS_PER_HOUR.name)!!)
        set(unit) = preferences.edit {
            putString(PreferecesSpeedUnit, unit.name)
        }


    var shouldKeepUpdatingWhileScreenIsOff: Boolean
        get() = preferences.getBoolean(PreferecesUpdatingScreen, false)
        set(value) = preferences.edit {
            putBoolean(PreferecesUpdatingScreen, value)
        }


}
