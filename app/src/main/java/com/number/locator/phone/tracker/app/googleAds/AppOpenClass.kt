package com.number.locator.phone.tracker.app.googleAds

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.number.locator.phone.tracker.app.MyApp.AppClass
import com.number.locator.phone.tracker.app.ui.activities.SplashActivity
import com.phone.tracker.locate.number.app.R
import com.number.locator.phone.tracker.app.utills.dismissAppOpenLoadingDialog
import com.number.locator.phone.tracker.app.utills.isOnline
import com.number.locator.phone.tracker.app.utills.showAppOpenLoadingDialog

class AppOpenClass(private val appClass: AppClass) : Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

    private var appOpenAd: AppOpenAd? = null

    private var currentActivity: Activity? = null
    private var isShowingAd = false
    private var adVisible = false
    private var myApp: AppClass? = appClass
    private var fullScreenContentCallback: FullScreenContentCallback? = null

    init {
        this.myApp?.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun fetchAd() {


        Log.d("appOpen", "ad available: "+isAdAvailable())

        if (isAdAvailable()) {
            return
        }

        if(!appClass.isOnline()){
            return
        }

        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                Log.d("appOpen", "load complete")
                appOpenAd = ad
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("appOpen", "failed $p0")
            }
        }

        val request: AdRequest = getAdRequest()

        myApp?.applicationContext?.apply {
            AppOpenAd.load(
                this,
                appClass.getString(R.string.openAppId),
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                loadCallback
            )
        }
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable() /*&&  !InterstitialHelper.isAdShown */) {

            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    adVisible = false
                   dismissAppOpenLoadingDialog()
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    dismissAppOpenLoadingDialog()
                    isShowingAd = false
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            adVisible = true
            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let {
               showAppOpenLoadingDialog(it)
                appOpenAd?.show(it) }
        } else {
            fetchAd()
        }
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        if(!isShowingAd){
            currentActivity = p0
            Log.d("IsClickedStarted","true")
        }
    }

    override fun onActivityResumed(p0: Activity) {
        if(!isShowingAd){
            currentActivity = p0
            Log.d("IsClickedResumed","true")
        }
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event==Lifecycle.Event.ON_START){

            currentActivity?.let {
                if(it !is SplashActivity){
                    showAdIfAvailable()
                }
            }
        }
    }
}