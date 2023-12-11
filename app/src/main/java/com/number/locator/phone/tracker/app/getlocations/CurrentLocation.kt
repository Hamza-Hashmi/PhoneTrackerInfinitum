package com.phone.tracker.locate.number.app.getlocations

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

abstract class CurrentLocation : LocationListener {

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

}
