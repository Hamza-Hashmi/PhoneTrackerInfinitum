package com.phone.tracker.locate.number.app.getlocations

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.phone.tracker.locate.number.app.utills.DestroyInterFace
import com.phone.tracker.locate.number.app.utills.HandlePermissions
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class WatchSpeed(context: Context) : DestroyInterFace {

    private val locationHandler: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val currentSpeedUpdate: Observable<ChangeCurrentSpeed>
        get() = speedUpdates

    private val speedUpdates=
        BehaviorSubject.createDefault<ChangeCurrentSpeed>(ChangeCurrentSpeed.CurrentSpeedAvailability)

    private val handlePermissions = HandlePermissions(context)

    private val giver: String?
    private var latestSpeedd: Float? = null
    var enabled: Boolean = false
        private set

    var isGPSServiceChecked = false
        private set

    private val locationTracker = object : CurrentLocation() {
        override fun onLocationChanged(location: Location) {
            provideUpdateOfSpeed(location.speed)
        }

        override fun onProviderEnabled(provider: String) {
            isGPSServiceChecked = true
            provideUpdateOfSpeed(latestSpeedd)
        }

        override fun onProviderDisabled(provider: String) {
            isGPSServiceChecked = false
            provideUpdateOfSpeed(null)
        }
    }

    init {
        val criteria = Criteria()
        criteria.isSpeedRequired = true
        giver = locationHandler.getBestProvider(criteria, false)

        updateGPSState()
    }

    private fun updateGPSState() {
        isGPSServiceChecked = giver != null && locationHandler.isProviderEnabled(giver)
    }

    fun toggle(state: Boolean) {
        if (state) {
            enable()
        } else {
            disableWatchSpeed()
        }
    }

    @SuppressLint("MissingPermission")
    fun enable() {
        if (enabled) {
            return
        }
        enabled = true
        updateGPSState()

        if (handlePermissions.isLocationPermissionDone() && giver != null) {
            locationHandler.requestLocationUpdates(giver, 800, 0f, locationTracker)
            provideUpdateOfSpeed(null)
        }
    }

    @SuppressLint("MissingPermission")
    fun disableWatchSpeed() {
        if (!enabled) {
            return
        }
        enabled = false
        updateGPSState()
        if (handlePermissions.isLocationPermissionDone() && giver != null) {
            locationHandler.removeUpdates(locationTracker)
        }
    }

    private fun provideUpdateOfSpeed(speed: Float?) {
        if (speed == null || latestSpeedd != speed) {
            latestSpeedd = speed
            speedUpdates.onNext(if (speed == null) {
                ChangeCurrentSpeed.CurrentSpeedAvailability
            } else {
                ChangeCurrentSpeed.UpdateLiveSpeed(speed)
            })
        }
    }

    override fun destroy() {
        disableWatchSpeed()
        speedUpdates.onComplete()
    }

    fun hasLocationPermission() = handlePermissions.isLocationPermissionDone()

}
