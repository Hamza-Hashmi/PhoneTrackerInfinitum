package com.phone.tracker.locate.number.app.utills

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class HandlePermissions(private val context: Context) {

    fun isLocationPermissionDone(): Boolean =
        PermissionChecker.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    fun allPermissionAllowed(grantResults: IntArray): Boolean =
        grantResults.singleOrNull() == PERMISSION_GRANTED


    fun reqPermissionLocation(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), 0)
    }
}
