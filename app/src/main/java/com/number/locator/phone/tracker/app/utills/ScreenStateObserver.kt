package com.phone.tracker.locate.number.app.utills

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


class ScreenStateObserver(private val context: Context) : DestroyInterFace {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenOnState = Intent.ACTION_SCREEN_ON == intent.action

        }
    }

    private var isScreenOnState: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                screenStateSub.onNext(isScreenOnState)
            }
        }

    val screenState: Observable<Boolean>
        get() = screenStateSub

    private val screenStateSub = BehaviorSubject.createDefault(isScreenOnState)

    private val powerHandler
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    init {
        isScreenOnState =
            powerHandler.isInteractive
        registerObserver()
    }

    private fun registerObserver() {
        val filter = IntentFilter()
            .apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        context.registerReceiver(receiver, filter)
    }

    override fun destroy() {
        context.unregisterReceiver(receiver)
    }

}
