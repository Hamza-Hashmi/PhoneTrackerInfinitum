package com.number.locator.phone.tracker.app.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.number.locator.phone.tracker.app.googleAds.loadAndReturnAd
import com.number.locator.phone.tracker.app.googleAds.showLoadedNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityInterstitialAdWithCounter
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.PermissionUtils
import com.number.locator.phone.tracker.app.utills.addContactNmbr
import com.number.locator.phone.tracker.app.utills.callMe
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.number.locator.phone.tracker.app.utills.showGPSDIALOGE
import com.number.locator.phone.tracker.app.utills.showMsg
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityNumberLocatorBinding
import com.phone.tracker.locate.number.app.utills.Constants.CountryCodees
import com.phone.tracker.locate.number.app.utills.Constants.ListOfCountyNames
import com.phone.tracker.locate.number.app.utills.Constants.countriesCodeName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale


class ActivityNumberLocator : BaseActivity() {
    var binding: ActivityNumberLocatorBinding? = null
    private var smf: SupportMapFragment? = null
    private var client: FusedLocationProviderClient? = null
    private var countryName: String? = null
    private var countryCode: String? = null
    private var countryCodeString: String? = null
    private var mobileNumber: String? = null
    private var long: Double? = 0.0
    private var lat: Double? = 0.0
    private var bitMap: Bitmap? = null
    private var contactLocation: LatLng? = null
    private var flag: ImageView? = null
    lateinit var byteArray: ByteArray
    private var numberIsValid = false
    private var googleMap: GoogleMap? = null
    var nativeAd: NativeAd? = null
    private val TAG = "Number_Locator"
    private var mapFragment: SupportMapFragment? = null
    private var markerOptions: MarkerOptions? = null
    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivityNumberLocatorBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
        initEvents()

        loadAd()
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

    private fun initViews() {
        binding?.apply {
            countryName = preferences.getString(ListOfCountyNames, "Pakistan")
            countryCode = preferences.getString(CountryCodees, "+92")
            countryCodeString = preferences.getString(countriesCodeName, "PK")

            countryCodePicker.registerCarrierNumberEditText(enterNUmber)
            countryCodePicker.setCountryForNameCode(countryCode)
            countryCodePicker.setPhoneNumberValidityChangeListener { isValidNumber ->
                numberIsValid = isValidNumber
            }
        }

        when {
            PermissionUtils.checkAccessFineLocationGranted(this@ActivityNumberLocator) -> {
                when {
                    PermissionUtils.isLocationEnabled(this@ActivityNumberLocator) -> {
                        initializeGoogleMap()
                    }

                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this@ActivityNumberLocator)
                    }
                }
            }

            else -> {
                PermissionUtils.requestLocationPermission(locationPermissionLauncher)
            }
        }
    }

    private fun initEvents() {
        binding?.apply {
            backArrow.setOnClickListener {
                onBackPressed()
            }

            btnSearch.setOnClickListener {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGPSDIALOGE()
                    showMsg("Please Turn On Your Mobile GPS")
                } else {
                    showPriorityInterstitialAdWithCounter(true, getString(R.string.interstial_Id)) {
                        searchNumber()
                    }

                }
            }

            countryCodePicker.setOnCountryChangeListener {
                countryCode = countryCodePicker.selectedCountryCode
                countryName = countryCodePicker.selectedCountryName
                countryCodeString = countryCodePicker.selectedCountryNameCode
                preferences.putString(countriesCodeName, countryName!!)
                preferences.putString(CountryCodees, countryCode!!)
                preferences.putString(countriesCodeName, countryCodeString!!)
                Log.d("countryCodeString", "$countryCodeString")
            }

            callUs.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@ActivityNumberLocator,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    callMe("00$countryCode$mobileNumber")
                } else {
                    Toast.makeText(
                        this@ActivityNumberLocator,
                        "Required call permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            contactUs.setOnClickListener {
                addContactNmbr("00$countryCode$mobileNumber")
            }

            enterNUmber.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val locationManager =
                        getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showGPSDIALOGE()
                        showMsg("Please Turn On Your Mobile GPS")
                    } else {
                        showPriorityInterstitialAdWithCounter(
                            true,
                            getString(R.string.interstial_Id)
                        ) {

                            searchNumber()
                        }

                    }
                    true
                } else false
            })
        }
    }

    private fun loadAd() {
        if (isNetworkAvailable()) {
            loadAndReturnAd(
                this@ActivityNumberLocator,
                nativeIdLow = getString(R.string.dialog_native)
            ) {
                nativeAd = it
            }
        }
    }

    private fun initReviewManager() {
        reviewManager = ReviewManagerFactory.create(this)

        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                reviewInfo = task.result
            } else {
                // There was some problem, continue regardless of the result.
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun searchNumber() {
        binding?.apply {
            mobileNumber = enterNUmber.text.toString()
            if (!mobileNumber.equals("", ignoreCase = true)) {
                if (numberIsValid) {
                    countryCode = countryCodePicker.selectedCountryCode
                    countryName = countryCodePicker.selectedCountryName
                    countryCodeString = countryCodePicker.selectedCountryNameCode
                    flag = countryCodePicker.imageViewFlag
                    flag!!.invalidate()
                    val drawable = flag!!.drawable as BitmapDrawable
                    val bitmap = drawable.bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                    byteArray = stream.toByteArray()
                    bitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    if (isNetworkAvailable()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            contactLocation = getUserLocation(countryName)
                        }.invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {
                                contactLocation?.let {
                                    showOnMap(7f)
                                    try {
                                        nativeAd?.let { it1 ->
                                            showLoadedNativeAd(
                                                this@ActivityNumberLocator,
                                                binding?.layoutNative!!,
                                                R.layout.native_ad_larg_layout,
                                                it1
                                            )
                                        }
                                        detialBottomSheet.visibility = View.VISIBLE
                                        countryFlag.setImageBitmap(bitMap)
                                        contactNo.text = countryCode + " " + mobileNumber
                                        contryName.text = countryName
                                        contryCode.text = countryCodeString
                                    } catch (ee: Exception) {
                                    }
                                }
                            }
                        }

                    } else {
                        showMsg("Kindly Check your Internet Connection")
                    }
                } else {
                    showMsg("The number you have enter is incorrect please try again")
                }
            } else {

                showMsg("Please enter number")
            }
        }
    }

    private fun getUserLocation(countryName: String?): LatLng? {
        val geoCoder = Geocoder(this)
        val address: List<Address>?
        var positionNew: LatLng? = null
        try {
            address = countryName?.let { geoCoder.getFromLocationName(it, 5) }
            if (address == null) {
                return null
            }
            if (address.isNotEmpty()) {
                val location = address[0]
                positionNew = LatLng(location.latitude, location.longitude)
                CoroutineScope(Dispatchers.Main).launch {
                    googleMap?.addMarker(MarkerOptions().position(positionNew))
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(positionNew, 9.75f),
                        3000,
                        null
                    )
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Log.d(TAG, "findLocation: ${e.message}")
            }
        }
        return positionNew
    }

    private fun initializeGoogleMap() {
        smf = supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        client = LocationServices.getFusedLocationProviderClient(this)
        Log.d(TAG, "initializeGoogleMap: 00")
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
                Log.d(TAG, "getMyLocation: 1")
                contactLocation = LatLng(location.latitude, location.longitude)
                markerOptions = contactLocation?.let {
                    MarkerOptions().position(it)
                        .title(resources.getString(R.string.current_location))
                }
                Log.d(TAG, "getMyLocation: 2")
                showOnMap(15f)
            }
        }

    }

    private fun showOnMap(fl: Float) {
        Log.d(TAG, "showOnMap: $smf")
        smf?.getMapAsync { googleMap ->
            Log.d(TAG, "showOnMap:2 ")
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


    override fun onBackPressed() {
        if (binding?.detialBottomSheet?.visibility == View.VISIBLE) {
            binding?.detialBottomSheet?.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

}