package com.number.locator.phone.tracker.app.broadcasts

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone.tracker.locate.number.app.R
import com.number.locator.phone.tracker.app.ui.activities.SplashActivity
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.phone.tracker.locate.number.app.utills.Constants.notificationDetails
import com.phone.tracker.locate.number.app.utills.Constants.notificationImage
import com.phone.tracker.locate.number.app.utills.Constants.notificationTitle
import com.phone.tracker.locate.number.app.utills.Constants.showNotification

class FireBaseBroadcastReceiver : BroadcastReceiver(){

    var notification_Id: Long = 0
    val TAG:String="FireBaseReceiver"
    private lateinit var context: Context
    private lateinit var appPreferences : AppPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context?, intent: Intent?) {

        this.context= context!!
        appPreferences=
           AppPreferences(context)
        val dataBundle = intent?.extras

        if (dataBundle != null) {

            Log.e(TAG, "onReceive: ${dataBundle.toString()}")
            Log.e(TAG, "onReceive: ${dataBundle.getString("title")}")
            Log.e(TAG, "onReceive: ${dataBundle.getString("body")}")
            Log.e(TAG, "onReceive: ${dataBundle.getString("imageUrl")}")
            try {
                dataBundle.let {dta_bund->

                    appPreferences.putString(notificationTitle, dta_bund.getString("title").toString())
                    appPreferences.putString(notificationDetails, dta_bund.getString("body").toString())
                    appPreferences.putString(notificationImage, dta_bund.getString("imageUrl").toString())
                    appPreferences.putBoolean(showNotification, true)

                }
                       } catch (e: Exception) {
                Log.e("Notification Data Wrong", e.toString())
            }



        }
    }
    companion object{

    }

    @SuppressLint("LongLogTag")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(title:String, body: String) {

        Log.e(TAG, "sendNotification: title $title")
        Log.e(TAG, "sendNotification: body $body")

        val NOTIFICATION_CHANNEL_ID = "PhoneNumber Tracker"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pattern = longArrayOf(0, 1000, 500, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = ""
            channel.enableLights(true)
            channel.vibrationPattern = pattern
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder: Notification.Builder = Notification.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )

        val notificationIntent = Intent(context, SplashActivity::class.java)

//        notificationIntent.putExtra(Constants.notification_Id,notification_Id)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, 0
        )


        notificationBuilder.setContentIntent(pendingIntent);


        val notication_Builder=  notificationBuilder.setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(body)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.logo1)
            .setColor(Color.BLUE).build()

        notificationManager.notify(1000, notication_Builder)
    }

}