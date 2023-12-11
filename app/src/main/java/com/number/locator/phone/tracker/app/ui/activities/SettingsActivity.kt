package com.number.locator.phone.tracker.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityInterstitialAdWithCounter
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivitySettingsBinding
import com.phone.tracker.locate.number.app.ui.dialogs.RateUsDialoge
import com.number.locator.phone.tracker.app.utills.isOnline
import com.number.locator.phone.tracker.app.utills.privacyPolicy
import com.number.locator.phone.tracker.app.utills.shareApp
import com.number.locator.phone.tracker.app.utills.showMsg

class SettingsActivity : BaseActivity() {
    var binding : ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPriorityInterstitialAdWithCounter(
            true,
            getString(R.string.interstial_Id)
        )

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        clickEvents()
        setupNativeAD()
    }

    private fun setupNativeAD() {
        binding?.apply {
            if (isNetworkAvailable()) {
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
            lang.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, LanguagesActivity::class.java))
            }

            shareBtn.setOnClickListener {
                shareApp()
            }
            rateUs.setOnClickListener {
                val fragment = RateUsDialoge.showRatingDialoge()
                fragment.show(supportFragmentManager, "RateUs")
            }
            appVersion.setOnClickListener {
                showMsg("I.0")
            }
            privacyPolicyBtn.setOnClickListener {
                privacyPolicy("https://sites.google.com/view/number-locator-app/number-locator-phone-tracker")
            }
            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}