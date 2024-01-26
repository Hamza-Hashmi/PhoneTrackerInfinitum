package com.number.locator.phone.tracker.app.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityCurrentLocationBinding
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.shareLiveLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityInterstitialAdWithCounter
import com.number.locator.phone.tracker.app.utills.PermissionUtils
import com.number.locator.phone.tracker.app.utills.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CurrentLocationActivity : BaseActivity(), OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener {

    private var locationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: LatLng? = null
    var binding: ActivityCurrentLocationBinding? = null

    private var googleMap: GoogleMap? = null
    private var address: String? = ""
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        initViews()
        clickEvents()
        initNative()
    }

    private fun initNative() {
        binding?.apply {
            if (isOnline()) {
                layoutNative.isVisible = true
                loadAndShowNativeAd(
                    layoutNative,
                    R.layout.native_ad_larg_layout,
                    getString(R.string.home_native)
                )
            } else {
                layoutNative.visibility = View.INVISIBLE
            }
        }
    }

    private fun clickEvents() {
        binding?.apply {
            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            shareLocationBtn.setOnClickListener {
                shareLiveLocation(currentLocation, address)
            }
        }
    }


    private fun initViews() {
        when {
            PermissionUtils.checkAccessFineLocationGranted(this@CurrentLocationActivity) -> {
                when {
                    PermissionUtils.isLocationEnabled(this@CurrentLocationActivity) -> {
                        setLocationListener()
                    }

                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this@CurrentLocationActivity)
                    }
                }
            }

            else -> {
                PermissionUtils.requestLocationPermission(locationPermissionLauncher)
            }
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            if (PermissionUtils.isLocationEnabled(this)) {
                setLocationListener()
            }
        } else {
            val rationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            if (!rationale)
                PermissionUtils.showGPSNotEnabledDialog(this)
        }
    }

    private fun initializeGoogleMap() {
//        try {
            if (googleMap == null) {
                val fragManager = supportFragmentManager
                val mapFragment = fragManager.findFragmentById(R.id.googleMapFrag) as SupportMapFragment?
                mapFragment?.getMapAsync(this)
            } else {
                setupGoogleMap()
            }
//        } catch (_: Exception) {
//        }
    }

    private fun setupGoogleMap() {
        currentLocation?.let { location ->
            CoroutineScope(Dispatchers.IO).launch {
                val differentLocation = Geocoder(this@CurrentLocationActivity, Locale.getDefault())
                val locationList = differentLocation.getFromLocation(location.latitude, location.longitude, 1)
                val locationAddress = locationList?.get(0)
                address = locationAddress?.getAddressLine(0)

            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.Main).launch {
                    // Clear the previous marker if it exists
                    marker?.remove()

                    marker = googleMap?.addMarker(
                        MarkerOptions()
                            .title(address.toString())
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    )

                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16F))
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap?.setOnMapLongClickListener(this)
        setupGoogleMap()
    }

    override fun onMapLongClick(p0: LatLng) {
        val coder = Geocoder(this, Locale.getDefault())
//        try {
        val locationList = coder.getFromLocation(p0.latitude, p0.longitude, 1)
        if (!locationList.isNullOrEmpty()) {
            val locationPlace = locationList[0]
            var addressDtl: String? = ""
            addressDtl += locationPlace?.getAddressLine(0)
            Toast.makeText(this, "" + addressDtl, Toast.LENGTH_SHORT).show()
            googleMap?.addMarker(MarkerOptions().position(p0))
        }
//        } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    private fun setLocationListener() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    Log.d("currentLocation", "onLocationResult: $currentLocation")
                    initializeGoogleMap()
                }
                // Things don't end here
                // You may also update the location on your web app
            }
        }
        locationCallback?.let { callback ->
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // Location Permission
            123 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setupGoogleMap()
                            // Setting things up
                        }

                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
    }
}