package com.number.locator.phone.tracker.app.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.number.locator.phone.tracker.app.MyApp.AppClass
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.loadPriorityAdmobInterstitial
import com.number.locator.phone.tracker.app.googleAds.showLoadedNativeAd
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.LanguageHelper
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        newBase?.let {
            LanguageHelper.onAttach(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupNative()

        if (isNetworkAvailable()) {
            loadPriorityAdmobInterstitial(
                getString(R.string.interstial_Id)
            )
            lifecycleScope.launch {
                delay(9000)
                binding?.progressBar?.isVisible = false
                binding?.letsStart?.isVisible = true
            }
        } else {
            lifecycleScope.launch {
                delay(2000)
                binding?.progressBar?.isVisible = false
                binding?.letsStart?.isVisible = true
            }
        }

        moveNext()
    }

    private fun setupNative() {
        binding?.apply {
            if (isNetworkAvailable()) {
                layoutNative.isVisible = true
                loadAndShowNativeAd(
                    layoutNative,
                    R.layout.native_ad_layout_small,
                    getString(R.string.splash_nativeId)
                )
            } else {
                layoutNative.visibility = View.INVISIBLE
            }
        }
    }

    private fun moveNext() {
        binding?.letsStart?.setOnClickListener {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}