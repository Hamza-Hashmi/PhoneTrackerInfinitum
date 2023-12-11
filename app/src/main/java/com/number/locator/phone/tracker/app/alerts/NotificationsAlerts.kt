package com.number.locator.phone.tracker.app.alerts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.number.locator.phone.tracker.app.services.CustomSpeedometerService
import com.phone.tracker.locate.number.app.R
import com.number.locator.phone.tracker.app.ui.activities.ActivitySpeedoMeter

class NotificationsAlerts(context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val builder: Notification.Builder

    init {
        val intent = Intent(context, ActivitySpeedoMeter::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel(context))
        }

        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
            .setSmallIcon(R.drawable.logo1)
            .setContentTitle(context.getString(R.string.current_speed))
            .setContentText(context.getString(R.string.unknown))
            .setContentIntent(pendingIntent)
    }

    fun initializeNotification(service: CustomSpeedometerService) {
        service.startForeground(NOTIFICATION_ID, builder.build())
    }

    fun updateNotification(message: String, @DrawableRes smallIcon: Int) {
        val notification = builder
            .setContentText(message)
            .setSmallIcon(smallIcon)
            .setOnlyAlertOnce(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "notification"

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createChannel(context: Context): NotificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                "PhoneNumber Tracker",
                NotificationManager.IMPORTANCE_MIN
            )
                .apply {
                    enableLights(false)
                    enableVibration(false)
                }

    }

}
