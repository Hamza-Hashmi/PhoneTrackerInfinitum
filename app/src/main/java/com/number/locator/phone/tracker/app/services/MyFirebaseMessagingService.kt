package com.phone.tracker.locate.number.app.services
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone.tracker.locate.number.app.R
import com.number.locator.phone.tracker.app.utills.AppPreferences
import com.phone.tracker.locate.number.app.utills.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG: String="MessagingService"

    var notification_Id: Long = 0

    private lateinit var appPreferences: AppPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        appPreferences=
            AppPreferences(context = this)
        // TODO Step 3.5 check messages for data
        // Check if message contains a data payload.
        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)


        }

        // TODO Step 3.6 check messages for notification and call sendNotification
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.e(TAG, "Message Notification Body: ${it.body}")
            Log.e(TAG, "Message Notification Body: ${it.title}")
            Log.e(TAG, "Message Notification Body: ${it.imageUrl}")

            appPreferences.putString(Constants.notificationTitle, it.title.toString())
            appPreferences.putString(Constants.notificationDetails, it.title.toString())
            appPreferences.putString(Constants.notificationImage, it.imageUrl.toString())
            appPreferences.putBoolean(Constants.showNotification, true)

                sendNotification(it)



        }


    }


    // [END receive_message]

    //TODO Step 3.2 log registration token
    // [START on_new_token]
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        Log.e(TAG, "onNewToken: $token ", )
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {

        Log.e(TAG, "sendRegistrationToServer: token $token")
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(remoteMessage: RemoteMessage.Notification) {

        val NOTIFICATION_CHANNEL_ID = "PhoneNumber Tracker"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pattern = longArrayOf(0, 1000, 500, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = ""
            channel.enableLights(true)
            channel.vibrationPattern = pattern
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder: Notification.Builder = Notification.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )

//        val notificationIntent = Intent(this, NotificationActivity::class.java)
//
//        notificationIntent.putExtra(Constants.notification_Id,notification_Id)
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(
//            this, 0,
//            notificationIntent,
//            PendingIntent.FLAG_MUTABLE or  PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//
//        notificationBuilder.setContentIntent(pendingIntent);


        val notication_Builder=  notificationBuilder.setAutoCancel(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(remoteMessage.getBody())
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.logo1)
            .setColor(Color.BLUE).build()

        notificationManager.notify(1000, notication_Builder)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun getPendingIntent():Int{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        }else{
            PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}