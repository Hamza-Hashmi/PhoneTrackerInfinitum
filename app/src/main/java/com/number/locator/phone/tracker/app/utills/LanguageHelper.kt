package com.number.locator.phone.tracker.app.utills

import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import com.number.locator.phone.tracker.app.model.LanguageModel
import java.util.*

object LanguageHelper {

    fun onAttach(context: Context): Context {

        val locale: String = AppPreferences(context).getString("langCode", "en").toString()

        return if (locale.isNotEmpty()) {
            setLocale(context, locale)
        } else {
            setLocale(context, "en")
        }
    }


    /**
     * Set the app's locale to the one specified by the given String.
     *
     * @param context
     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
     * support country and variant codes so far); the special string "system" sets
     * the locale to the locale specified in system settings
     * @return
     */

    fun setLocale(context: Context, localeSpec: String): Context {
        val locale: Locale = if (localeSpec == "system") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0]
            } else {
                Resources.getSystem().configuration.locale
            }
        } else {
            Locale(localeSpec)
        }
        Locale.setDefault(locale)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale)
        } else {
            updateResourcesLegacy(context, locale)
        }
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
    val languagesList = mutableListOf(
        LanguageModel("Arabic", "ar"),
        LanguageModel("Bengali", "bn"),
        LanguageModel("Chinese", "zh"),
        LanguageModel("English", "en"),
        LanguageModel("German", "de"),
        LanguageModel("Hindi", "hi"),
        LanguageModel("Japanese", "ja"),
        LanguageModel("Russian", "ru"),
        LanguageModel("Spanish", "es"),
        LanguageModel("Turkish", "tr"),
    )
}