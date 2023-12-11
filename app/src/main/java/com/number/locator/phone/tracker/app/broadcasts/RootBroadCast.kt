package com.number.locator.phone.tracker.app.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.number.locator.phone.tracker.app.services.CustomSpeedometerService
import com.phone.tracker.locate.number.app.utills.AppSettings


class RootBroadCast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        if (AppSettings(context).isRunning) {
            CustomSpeedometerService.initStateOfRunning(context, true)
        }
    }

}