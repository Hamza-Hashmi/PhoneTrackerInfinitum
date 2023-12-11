package com.phone.tracker.locate.number.app.utills

import android.content.Context
import com.phone.tracker.locate.number.app.R


object FormateOFSPeed {

    fun speedFormatint(context: Context, speed: Float, units: StandardSpeedUnits? = null): String {
        val speedString = if (speed < 100f) {
            String.format("%1$.1f", speed)
        } else {
            String.format("%1$.0f", speed)
        }
        return units
            ?.let {
                context.getString(
                    R.string.speed_unit_formating,
                    speedString,
                    context.getString(it.nameRes)
                )
            }
            ?: speedString
    }

}