package com.number.locator.phone.tracker.app.utills

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.phone.tracker.locate.number.app.BuildConfig
import com.phone.tracker.locate.number.app.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.phone.tracker.locate.number.app.utills.DestroySection
import io.reactivex.disposables.Disposable





fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
    return false
}



fun Context.rateUs() {

    val rateUsIntent = Intent(
        Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName)
    )
    startActivity(rateUsIntent)
}


fun Activity.callMe(mobileNumber : String?) {

    val callIntent = Intent(Intent.ACTION_CALL)
    callIntent.data = Uri.parse("tel:$mobileNumber")
    startActivity(callIntent)
}
fun Activity.addContactNmbr(mobileNumber : String?) {
    val intent = Intent(Intent.ACTION_INSERT)
    intent.type = ContactsContract.Contacts.CONTENT_TYPE
    intent.putExtra(ContactsContract.Intents.Insert.PHONE, mobileNumber)
    startActivity(intent)

}
fun Activity.sendSMSToSavedContacts(mobileNumber: String?){
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.type = "vnd.android-dir/mms-sms"
    intent.data = Uri.parse("sms:$mobileNumber")
    startActivity(intent)

}
fun Activity.showMsg(message: String?) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message!!,2500).show()

}

fun Context.privacyPolicy(link: String) {

    val checkPrivacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(checkPrivacyPolicyIntent)
}

fun Context.shareApp() {
    val sharingIntent = Intent()
    sharingIntent.action = Intent.ACTION_SEND
    sharingIntent.putExtra(
        Intent.EXTRA_TEXT,
        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
    )
    sharingIntent.type = "text/plain"
    startActivity(sharingIntent)
}
fun Activity.sendMail() {
    val feedbackEmail = "infinitumSoft.technologies@gmail.com"
    val addresses = arrayOf(feedbackEmail)
    try {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        emailIntent.type = "plain/text"
        emailIntent.setClassName(
            "com.google.android.gm",
            "com.google.android.gm.ComposeActivityGmail"
        )

        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phone Tracker")
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Tell us which issues you are facing using Phone Tracker App"
        )
        startActivity(emailIntent)
    } catch (e: Exception) {

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:" + addresses[0])
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phone Tracker")
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Tell us which issues you are facing using Phone Tracker App"
        )
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}
const val REQUEST_GPS = 200
fun Activity.showGPSDIALOGE() {
    val builder = AlertDialog.Builder(this, R.style.AlertDialogLayout)
    builder.setTitle(getString(R.string.number_detector))
    .setIcon(R.drawable.logo1)
        .setMessage("GPS is disabled in your device. Enable it?")
        .setCancelable(false)
        .setPositiveButton("Enable GPS",
            DialogInterface.OnClickListener { dialog, id ->
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivityForResult(callGPSSettingIntent, REQUEST_GPS)
            })
    builder.setNegativeButton(
        "Cancel"
    ) { dialog, id -> dialog.cancel() }
    val alert1 = builder.create()
    alert1.setOnShowListener {
        alert1.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.themeColor))
        alert1.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.themeColor))
    }
    try {
        alert1.show()
    }catch (e: WindowManager.BadTokenException){
        Log.e("TAG", "showDialogForGPSIntent: ${e.message}")
    }
}
fun Activity.shareLiveLocation(location : LatLng?, name : String?){
    if (location != null && !name.isNullOrEmpty()){
        val uri = "https://www.google.com/maps/?location=${location.latitude} ${location.longitude} $name"
        val sharingSub = "Here is my location"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,sharingSub)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, uri)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }else{
        Toast.makeText(this, "Please check your gps", Toast.LENGTH_SHORT).show()
    }

}
fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true

    return true
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    edit().apply {
        block()
        apply()
    }
}

fun Disposable.ownedBy(destroySection: DestroySection) {
    destroySection.own {
        dispose()
    }
}

val Service.context: Context
    get() = this


private var appOpenDialog : Dialog? = null
fun showAppOpenLoadingDialog(activity: Activity) {
    dismissAppOpenLoadingDialog()
    appOpenDialog = Dialog(activity)
    appOpenDialog?.setContentView(R.layout.loading_screen)
    appOpenDialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    appOpenDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    appOpenDialog?.setCancelable(false)
    appOpenDialog?.show()
}

fun dismissAppOpenLoadingDialog(){
    try{
        appOpenDialog?.dismiss()
    }catch (e:Exception){
        e.printStackTrace()
    }
}

