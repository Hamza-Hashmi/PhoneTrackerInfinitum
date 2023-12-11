package com.number.locator.phone.tracker.app.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import com.number.locator.phone.tracker.app.utills.context
import com.number.locator.phone.tracker.app.utills.ownedBy

import com.phone.tracker.locate.number.app.R
import com.number.locator.phone.tracker.app.alerts.NotificationsAlerts
import com.phone.tracker.locate.number.app.getlocations.ChangeCurrentSpeed
import com.phone.tracker.locate.number.app.getlocations.WatchSpeed
import com.phone.tracker.locate.number.app.icons.ProvideIcons
import com.phone.tracker.locate.number.app.services.DestroyService
import com.phone.tracker.locate.number.app.utills.*
import kotlin.math.roundToInt


class CustomSpeedometerService : DestroyService() {

    private lateinit var notificationAlert: NotificationsAlerts
    private val iconProvider: ProvideIcons by lazy {
        ProvideIcons(context)
    }

    private val screenStateObserver: ScreenStateObserver by lazy {
        destroySection.own1(ScreenStateObserver(context))
    }

    private val appSettings by lazy {
        AppSettings(this)
    }

    private val watchSpeedObj: WatchSpeed by lazy {
        (WatchSpeed(context))
    }

    private val units: StandardSpeedUnits
        get() = appSettings.units

    override fun onCreate() {
        super.onCreate()

        notificationAlert = NotificationsAlerts(this)

        notificationAlert.initializeNotification(this)

        watchSpeedObj.currentSpeedUpdate
            .subscribe {
                updateNotifications((it as? ChangeCurrentSpeed.UpdateLiveSpeed)?.speed)
            }
            .ownedBy(destroySection)

        if (appSettings.shouldKeepUpdatingWhileScreenIsOff) {
            watchSpeedObj.enable()
        } else {
            createScreenStateWatcher()
        }

        appSettings.isRunning = true
        destroySection.own {
            appSettings.isRunning = false
        }
    }

    private fun createScreenStateWatcher() {
        screenStateObserver.screenState
            .subscribe { isScreenOn ->
                if (isScreenOn) {
                    watchSpeedObj.enable()
                } else {
                    watchSpeedObj.disableWatchSpeed()
                }
            }
            .ownedBy(destroySection)
    }

    private fun updateNotifications(currentSpeed: Float?) {
        val message: String
        val iconRes: Int
        if (currentSpeed == null) {
            message = getString(specialStateMessage)
            iconRes = R.drawable.logo1
        } else {
            val convertedSpeed = units.convertSpeed(currentSpeed)

            message = FormateOFSPeed.speedFormatint(context, convertedSpeed, units)
            iconRes = iconProvider.getIconForNumber(convertedSpeed.roundToInt())
        }
        notificationAlert.updateNotification(message, iconRes)
    }

    private val specialStateMessage: Int
        @StringRes
        get() = when {
            !watchSpeedObj.isGPSServiceChecked -> R.string.enableGPS
            else -> R.string.unknown
        }

    companion object {

        fun initStateOfRunning(context: Context, state: Boolean) {
            val intent = Intent(context, CustomSpeedometerService::class.java)
            if (state) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } else {
                context.stopService(intent)
            }
        }


        fun isRunning(context: Context): Boolean =
            AppSettings(context).isRunning

        fun popupStateOfRunning(context: Context) {
            initStateOfRunning(context, !isRunning(context))
        }



        fun restartRuning(context: Context) {
            initStateOfRunning(context, false)
            initStateOfRunning(context, true)
        }
    }

}
