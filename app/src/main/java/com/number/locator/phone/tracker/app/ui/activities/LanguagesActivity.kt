package com.number.locator.phone.tracker.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.number.locator.phone.tracker.app.adapter.LanguageAdapter
import com.number.locator.phone.tracker.app.googleAds.loadAndShowNativeAd
import com.number.locator.phone.tracker.app.googleAds.showPriorityAdmobInterstitial
import com.number.locator.phone.tracker.app.model.LanguageModel
import com.number.locator.phone.tracker.app.ui.base.BaseActivity
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.number.locator.phone.tracker.app.utills.LanguageHelper
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R
import com.phone.tracker.locate.number.app.databinding.ActivityLanguagesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LanguagesActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguagesBinding
    private var adapter: LanguageAdapter? = null
    private var myList = mutableListOf<LanguageModel>()
    private var selectedLangCode: String = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initListeners()
    }



    private fun initViews() {
        val isFirstTime = intent?.getBooleanExtra("isFirstTime", false) == true
        if (isFirstTime) {
            showPriorityAdmobInterstitial(
                true,
               getString(R.string.interstial_Id)
            )
        }

        initNative()
        LanguageHelper.languagesList.forEach { model ->
            myList.add(model)
        }

        adapter = LanguageAdapter(myList) {
            selectedLangCode = it
        }
        binding.languageRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.languageRv.adapter = adapter
    }

    private fun initListeners() {
        binding.backArrow.setOnClickListener {
            finish()
        }



        binding.btnDone.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(10)
                if (selectedLangCode != AppPreferences(this@LanguagesActivity).getString("langCode", "en")) {
                    AppPreferences(this@LanguagesActivity).putString("langCode", selectedLangCode)
                    val restartIntent = Intent(this@LanguagesActivity, SplashActivity::class.java)
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(restartIntent)
                    finishAffinity()
                } else {
                    finish()
                }
            }
        }
    }

    private fun initNative() {
        if (isNetworkAvailable()) {
            binding.layoutNative.isVisible = true
            loadAndShowNativeAd(
                binding.layoutNative,
                R.layout.native_ad_layout_small,
                getString(R.string.language_native)
            )

        } else {
            binding.layoutNative.visibility = View.INVISIBLE
        }
    }

}