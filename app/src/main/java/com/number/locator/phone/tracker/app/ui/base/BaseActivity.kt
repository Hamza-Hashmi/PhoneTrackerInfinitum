package com.number.locator.phone.tracker.app.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.number.locator.phone.tracker.app.ui.viewModels.LocationViewModel
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.number.locator.phone.tracker.app.utills.LanguageHelper
import com.phone.tracker.locate.number.app.utills.DestroySection
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {
    internal val destroyer = DestroySection()
    val preferences : AppPreferences by inject()
    val locationViewModel : LocationViewModel by viewModel()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        newBase?.let {
            LanguageHelper.onAttach(it)
        }
    }

}