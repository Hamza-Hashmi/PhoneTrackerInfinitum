package com.number.locator.phone.tracker.app.dataModel

import android.location.Location


data class CurrentLocationModel(var long: Double, var lat: Double, var currentSpeed : Double, var location: Location)
