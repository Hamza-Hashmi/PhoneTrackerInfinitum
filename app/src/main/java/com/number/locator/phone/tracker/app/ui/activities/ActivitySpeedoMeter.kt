package com.number.locator.phone.tracker.app.ui.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityInterstitialAdWithCounter
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivitySpeedoMeterBinding
import com.phone.tracker.locate.number.app.getlocations.ChangeCurrentSpeed
import com.phone.tracker.locate.number.app.getlocations.WatchSpeed
import com.number.locator.phone.tracker.app.services.CustomSpeedometerService
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.number.locator.phone.tracker.app.utills.ownedBy
import com.phone.tracker.locate.number.app.utills.*

class ActivitySpeedoMeter : BaseActivity() {
    var binding : ActivitySpeedoMeterBinding? = null

    private val handlePermissions: HandlePermissions by lazy {
        HandlePermissions(this)
    }
    private val appSettings by lazy { AppSettings(this) }
    private var units : StandardSpeedUnits
        get() = appSettings.units
        set(value) {
            if (value != appSettings.units) {
                appSettings.units = value
                restartRuningSpeedWatchers()
                updateViews()
            }
        }
   private var watchSpeed : WatchSpeed? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivitySpeedoMeterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initViews()
        nativeLayout()

    }

    private fun nativeLayout() {
        binding?.apply {
            if (isNetworkAvailable()) {
                layoutNative.isVisible = true
                loadAndShowNativeAd(
                    layoutNative,
                    R.layout.native_ad_layout_small,
                    getString(R.string.speedoMeter_screen_nativeId)
                )

            } else {
                layoutNative.visibility = View.INVISIBLE
            }
        }
    }

    private fun initViews() {
        binding?.apply {
            watchSpeed = WatchSpeed(this@ActivitySpeedoMeter)
            setupUnitSelector()
            setupCheckbox()
            stopBtn.setOnCheckedChangeListener { _, isChecked -> toggleState(isChecked) }

            watchSpeed?.currentSpeedUpdate
                ?.subscribe { speedUpdate ->
                    when (speedUpdate) {
                        is ChangeCurrentSpeed.UpdateLiveSpeed -> {
                            updateSpeedViews(speedUpdate.speed)
                        }
                        is ChangeCurrentSpeed.CurrentSpeedAvailability -> {
                            updateSpeedViews(0f)
                        }
                        else -> {}
                    }
                }
                ?.ownedBy(destroyer)

        }
    }

    private fun updateSpeedViews(speed: Float) {
        val convertedSpeed = units.convertSpeed(speed)
        binding?.apply {
            speedoMeter.value = convertedSpeed
            try {
                selectedDistanceText.text = when {
                    !stopBtn.isChecked -> SPEETOMETERPLACEHOLDER
                    !watchSpeed!!.isGPSServiceChecked -> "Turn On Mobile GPS"
                    !watchSpeed!!.hasLocationPermission() -> "Permission not Granted"
                    else -> FormateOFSPeed.speedFormatint(this@ActivitySpeedoMeter, convertedSpeed)
                }

            }catch (e: Exception){

            }
        }

    }


    private fun restartRuningSpeedWatchers() {
        if (watchSpeed?.enabled == true) {
            watchSpeed?.disableWatchSpeed()
            watchSpeed?.enable()
        }

        if (CustomSpeedometerService.isRunning(this)) {
            CustomSpeedometerService.restartRuning(this)
        }
    }
    private fun updateViews() {
        val isRunning = appSettings.isRunning
        binding?.apply {
            stopBtn.isChecked = isRunning
            speedoMeter.maxValue = units.maxValue.toFloat()
            speedoMeter.markCount = units.steps + 1
            checkBox.visibility = if (isRunning) View.GONE else View.VISIBLE
            keepScreenOn(isRunning)
        }

    }

    private fun keepScreenOn(enabled: Boolean) {
        if (enabled) {
           window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    override fun onStart() {
        super.onStart()
        initState()
        updateViews()
    }

    private fun initState() {
        val state = CustomSpeedometerService.isRunning(this)
        CustomSpeedometerService.initStateOfRunning(this, state)
        watchSpeed?.toggle(state)
    }

    override fun onStop() {
        super.onStop()
        watchSpeed?.disableWatchSpeed()
    }

    private fun toggleState(state: Boolean) {
        binding?.apply {
            if (state && !handlePermissions.isLocationPermissionDone()) {
                stopBtn.isChecked = false
                handlePermissions.reqPermissionLocation(this@ActivitySpeedoMeter)
                return
            }

            appSettings.isRunning = state
            watchSpeed?.toggle(state)
            CustomSpeedometerService.initStateOfRunning(this@ActivitySpeedoMeter, state)
            updateSpeedViews(0f)
            updateViews()
        }

    }

    private fun setupUnitSelector() {
        binding?.apply {
            val unitNames = StandardSpeedUnits.values().map { getText(it.nameRes) }
            firstDis.setOnClickListener {
                firstDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.cardBg))
                secondDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                thirdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                forthdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                units = StandardSpeedUnits.values()[0]
            }
            secondDis.setOnClickListener {
                firstDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                secondDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.cardBg))
                thirdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                forthdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                units = StandardSpeedUnits.values()[1]
            }
            thirdDis.setOnClickListener {
                firstDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                secondDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                thirdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.cardBg))
                forthdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                units = StandardSpeedUnits.values()[2]
            }
            forthdDis.setOnClickListener {
                firstDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                secondDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                thirdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.white))
                forthdDis.setBackgroundColor(ContextCompat.getColor(this@ActivitySpeedoMeter,R.color.cardBg))
                units = StandardSpeedUnits.values()[3]
            }

            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }

    }

    private fun setupCheckbox() {
        binding?.apply {
            stopBtn.isChecked = appSettings.shouldKeepUpdatingWhileScreenIsOff
            stopBtn.setOnCheckedChangeListener { _, checked ->
                appSettings.shouldKeepUpdatingWhileScreenIsOff = checked
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (handlePermissions.allPermissionAllowed(grantResults)) {
            binding?.stopBtn?.isChecked = true
        }
    }
    companion object {

        private const val SPEETOMETERPLACEHOLDER = "---"

    }

}


