package com.number.locator.phone.tracker.app.ui.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.number.locator.phone.tracker.app.googleAds.loadAndReturnAd
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showLoadedNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityAdmobInterstitial
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityMainBinding
import com.phone.tracker.locate.number.app.databinding.ExitDialogBinding
import com.phone.tracker.locate.number.app.ui.dialogs.RateUsDialoge
import com.phone.tracker.locate.number.app.utills.Constants.isFromBackPress
import com.phone.tracker.locate.number.app.utills.Constants.permissionList

class MainActivity : BaseActivity() {
    private lateinit var btnNumberLocator: ConstraintLayout
    private lateinit var btnCurrentLocation: ConstraintLayout
    private lateinit var btnSpeedoMeter: ConstraintLayout
    private lateinit var btnTool: ConstraintLayout
    private lateinit var layoutNative: CardView
    private lateinit var btnSettings: ImageView
    var nativeAd: NativeAd? = null

    private lateinit var dialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: ExitDialogBinding

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(newBase)
//        newBase?.let {
//            LanguageHelper.onAttach(it)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityAdmobInterstitial(
            true,
            getString(R.string.interstial_Id)
        )

        setContentView(binding.root)
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation)
        btnNumberLocator = findViewById(R.id.btnNumberLocator)
        btnSpeedoMeter = findViewById(R.id.btnSpeedoMeter)
        btnTool = findViewById(R.id.btnTools)
        btnSettings = findViewById(R.id.settingICon)
        layoutNative = findViewById(R.id.layout_native)

        bottomSheetBinding = ExitDialogBinding.inflate(layoutInflater, binding.root, false)
        dialog = BottomSheetDialog(this@MainActivity)
        dialog.setContentView(bottomSheetBinding.root)

        clickEvents()

        initNative()

        if (isNetworkAvailable()) {
            loadAndReturnAd(
                this@MainActivity,
                nativeIdLow = getString(R.string.exit_nativeId)
            ) {
                nativeAd = it
            }
        }

    }


    private fun initNative() {
        if (isNetworkAvailable()) {
            layoutNative.isVisible = true
            loadAndShowNativeAd(
                layoutNative,
                R.layout.native_ad_layout_small,
                getString(R.string.home_native)
            )

        } else {
            layoutNative.visibility = View.INVISIBLE
        }
    }


    private fun clickEvents() {
        btnNumberLocator.setOnClickListener {
            if (requirePemissionDone()) {
                startActivity(Intent(this@MainActivity, ActivityNumberLocator::class.java))

            } else {
                Log.d("PermissionDtl", "Required")
                requestPermission()
            }

        }
        btnCurrentLocation.setOnClickListener {
            startActivity(Intent(this@MainActivity, CurrentLocationActivity::class.java))
        }
        btnTool.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActivityLocatorTools::class.java))
        }
        btnSpeedoMeter.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActivitySpeedoMeter::class.java))
        }
        btnSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

    }


    private fun requestPermission() {
        permissionRequestLauncher.launch(permissionList)
    }

    private fun requirePemissionDone() = permissionList.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            permission.entries.forEach {

            }
            val granted = permission.entries.all {
                it.value == true
            }
            if (granted) {
                Log.d("PermissionDtl", "Granted")
                startActivity(Intent(this@MainActivity, ActivityNumberLocator::class.java))
            } else {
                Log.d("PermissionDtl", "Required2")
                requestPermission()
            }

        }

    override fun onBackPressed() {
        if (!preferences.getBoolean("ratingCheck")) {
            val fragment = RateUsDialoge.showRatingDialoge()
            fragment.show(supportFragmentManager, "RateUs")
            isFromBackPress = true
        } else {
            dialog.show()
            if (isNetworkAvailable()) {
                bottomSheetBinding.layoutNative.isVisible = true
                nativeAd?.let { it1 ->
                    showLoadedNativeAd(
                        this@MainActivity,
                        bottomSheetBinding.layoutNative,
                        R.layout.native_ad_larg_layout,
                        it1
                    )
                }

            } else {
                bottomSheetBinding.layoutNative.visibility = View.INVISIBLE
            }

            bottomSheetBinding.cancel.setOnClickListener {
                dialog.dismiss()
            }

            bottomSheetBinding.exit.setOnClickListener {
                dialog.dismiss()
                finishAffinity()
            }

        }
    }
}
