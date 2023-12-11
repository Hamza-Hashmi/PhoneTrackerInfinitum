package com.phone.tracker.locate.number.app.utills

import android.Manifest


object Constants {


        var tools = "Tools"

        const val preferenceName = "PhoneTracker"

        var notification_Id: String = "PhoneTracker"
        var notificationTitle = "notificationTitle"
        var notificationDetails = "notificationDetails"
        var notificationImage = "notificationImage"
        var showNotification = "showNotification"


       var CountryCodees = "countriesCode"
       var ListOfCountyNames = "countriesName"
       var countriesCodeName = "countriesCodeName"

        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        var isFromBackPress = false


}