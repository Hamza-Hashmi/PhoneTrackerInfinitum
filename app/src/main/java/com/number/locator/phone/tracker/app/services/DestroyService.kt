package com.phone.tracker.locate.number.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.phone.tracker.locate.number.app.utills.DestroySection

abstract class DestroyService : Service() {
    internal val destroySection = DestroySection()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        destroySection.destroy()
    }
}