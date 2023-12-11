package com.number.locator.phone.tracker.app.ui.activities

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityCurrentLocationBinding
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.shareLiveLocation
import com.number.locator.phone.tracker.app.utills.showGPSDIALOGE
import com.number.locator.phone.tracker.app.utills.showMsg
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
import com.number.locator.phone.tracker.app.utills.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class CurrentLocationActivity : BaseActivity(), OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener {

    private var locationManager: LocationManager? = null
    var binding: ActivityCurrentLocationBinding? = null

    private var googleMap: GoogleMap? = null
    private var address: String? = ""
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var exactLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        initEvents()
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
                shareLiveLocation(exactLocation, address)
            }
        }
    }


    private fun initEvents() {
        try {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.googleMapFrag) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } catch (_: Exception){}
    }

    private var marker: Marker? = null
    override fun onMapReady(p0: GoogleMap) {
        try {
            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) != true) {
                showGPSDIALOGE()
                showMsg("Please Turn On Your Mobile GPS")
            } else {
                locationViewModel.getLocationData().observe(this) {
                    try {
                        it?.let { location ->
                            Log.d("locationCurrent", "$location")
                           CoroutineScope(Dispatchers.IO).launch {
                               lat = location.lat
                               long = location.long
                               exactLocation = LatLng(lat, long)
                               val differentLocation = Geocoder(this@CurrentLocationActivity, Locale.getDefault())
                               val locationList = differentLocation.getFromLocation(lat, long, 1)
                               val locationAddress = locationList?.get(0)
                               address = locationAddress?.getAddressLine(0)

                           }.invokeOnCompletion { CoroutineScope(Dispatchers.Main).launch {
                               // Clear the previous marker if it exists
                               marker?.remove()

                               marker = googleMap?.addMarker(
                                   MarkerOptions()
                                       .title(address.toString())
                                       .position(exactLocation!!)
                                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                               )

                               exactLocation?.let { locationn ->
                                   googleMap?.animateCamera(
                                       CameraUpdateFactory.newLatLngZoom(
                                           locationn,
                                           16F
                                       )
                                   )
                               }
                           } }


                        }
                    } catch (_: IOException){}
                    catch (_: Exception){}
                }
            }
        }catch (_: Exception){}
        googleMap = p0
        googleMap?.setOnMapLongClickListener(this)
    }


    override fun onMapLongClick(p0: LatLng) {
        val coder = Geocoder(this, Locale.getDefault())
        try {
            val locationList = coder.getFromLocation(p0.latitude, p0.longitude, 1)
            if (!locationList.isNullOrEmpty()) {
                val locationPlace = locationList[0]
                var addressDtl: String? = ""
                addressDtl += locationPlace?.getAddressLine(0)
                Toast.makeText(this, "" + addressDtl, Toast.LENGTH_SHORT).show()
                googleMap?.addMarker(MarkerOptions().position(p0))
            }
        } catch (_: Exception) {}
    }
}