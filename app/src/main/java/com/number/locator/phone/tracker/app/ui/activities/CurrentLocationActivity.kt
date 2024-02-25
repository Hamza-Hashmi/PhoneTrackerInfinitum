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
import androidx.core.app.ActivityCompat
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

class CurrentLocationActivity : BaseActivity(),
    GoogleMap.OnMapLongClickListener {
    private var smf: SupportMapFragment? = null
    private var client: FusedLocationProviderClient? = null
    private var contactLocation: LatLng? = null
    private var locationCallback: LocationCallback? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: LatLng? = null
    var binding: ActivityCurrentLocationBinding? = null

    private var googleMap: GoogleMap? = null
    private var address: String? = ""
    private var markerOptions: MarkerOptions? = null
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
                        initializeGoogleMap()
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
                initializeGoogleMap()
            }
        } else {
            val rationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            if (!rationale)
                PermissionUtils.showGPSNotEnabledDialog(this)
        }
    }

    private fun initializeGoogleMap() {
        smf = supportFragmentManager.findFragmentById(R.id.googleMapFrag) as SupportMapFragment?
        client = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()

    }


    fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val task = client?.lastLocation
        task?.addOnSuccessListener {
            it?.let { location ->
                contactLocation = LatLng(location.latitude, location.longitude)
                markerOptions = contactLocation?.let {
                    MarkerOptions().position(it)
                        .title(resources.getString(R.string.current_location))
                }
                showOnMap(15f)
            }
        }

    }
    private fun showOnMap(fl: Float) {
        smf?.getMapAsync { googleMap ->
            markerOptions = contactLocation?.let {
                MarkerOptions().position(it).title(resources.getString(R.string.current_location))
            }
            markerOptions?.let {
                googleMap.addMarker(markerOptions!!)
            }
            contactLocation?.let {
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it, fl
                    )
                )
            }
        }
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


    override fun onDestroy() {
        super.onDestroy()

        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
    }
}