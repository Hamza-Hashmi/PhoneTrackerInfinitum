package com.number.locator.phone.tracker.app.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityInterstitialAdWithCounter
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityLocatorToolsBinding

class ActivityLocatorTools : BaseActivity() {

    var binding : ActivityLocatorToolsBinding? = null
    private val settingsPath = "com.android.settings"
    private val dataUsagePath = "com.android.settings.Settings\$DataUsageSummaryActivity"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivityLocatorToolsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initNative()
        clickEvnets()
    }

    private fun initNative() {
        binding?.apply {
            if (isNetworkAvailable()) {
                layoutNative.isVisible = true
                loadAndShowNativeAd(
                    layoutNative,
                    R.layout.native_ad_larg_layout,
                    getString(R.string.locatortool_nativeId)
                )
            } else {
                layoutNative.visibility = View.GONE
            }
        }
    }

    private fun clickEvnets() {
        binding?.apply {
            managePhone.setOnClickListener {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
            dataUsage.setOnClickListener {
                openDataUsageSettings()
            }
            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showDataUsageDetails() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        val componentName = ComponentName(settingsPath, dataUsagePath)
        intent.component = componentName
       startActivity(intent)
    }

    private fun openDataUsageSettings() {
        val intent = Intent()
        // Starting from Android M, the data usage settings are in a different place
        intent.action = android.provider.Settings.ACTION_DATA_USAGE_SETTINGS

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception or display a message to the user
        }
    }
}