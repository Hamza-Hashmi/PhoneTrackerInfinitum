package com.number.locator.phone.tracker.app.googleAds

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.number.locator.phone.tracker.app.utills.isNetworkAvailable
import com.phone.tracker.locate.number.app.R


open class InterstitialAdClass {

    var mInterstitialAd: InterstitialAd? = null

    private val logTag = "interstitialAdFlow"

    private var someOpPerformed = false

    private val runnable = Runnable {
        showInterstitialAdLog("checking safe operation ... ")
        if (!someOpPerformed) {
            showInterstitialAdLog("safe operation needed , performing")
            mInterstitialAd = null
            failListener?.invoke()
        } else showInterstitialAdLog("safe operation not needed")
    }
    private lateinit var handler: Handler
    private var failListener: (() -> Unit)? = null

    companion object {
        var failCounter = 0

        @Volatile
        private var instance: InterstitialAdClass? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: InterstitialAdClass().also { instance = it }
        }
    }

    /**
     *pre load interstitial ad by priority and show later when needed for splash and other
     * */

    fun loadPriorityInterstitialAds(
        context: Context,
        adIDLow: String
    ) {
        handler = Handler(Looper.myLooper()!!)
        showInterstitialAdLog("Loading High Ad 2...")
            loadInterstitialAdPriority(context, adIDLow) {}

    }


private fun loadInterstitialAdPriority(
    context: Context, adId: String, adLoadedCallback: () -> Unit
) {
    context.let {
        InterstitialAd.load(
            it,
            adId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    adLoadedCallback.invoke()
                    showInterstitialAdLog("High Ad failed to load because $ad")
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAdPriority = ad
                    showInterstitialAdLog("High Ad successfully loaded")
                }
            })
    }
}

fun showPriorityInterstitialAdNew(
    activity: Activity,
    loadAgain: Boolean = false,
    adIDLow: String? = null,
    loadAd: ((InterstitialAd) -> Unit)? = null,
    closeListener: (() -> Unit)? = null,
    failListener: (() -> Unit)? = null,
    showListener: (() -> Unit)? = null
) {
    showInterstitialAdLog("Showing priority Ad ...")
    if (activity.isNetworkAvailable()) {
        interstitialAdPriority?.let {
            showLoadingDialog(activity)
            showInterstitialAdLog("priority Ad is not null , Calling show and setting listener ...")
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    showInterstitialAdLog("priority Ad closed by user")
                    isInterstitialAdOnScreen = false
                    interstitialAdPriority = null
                    closeListener?.invoke()
                    if (loadAgain) {
                        loadAgainPriorityInterstitialAd(activity, adIDLow)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    someOpPerformed = true
                    interstitialAdPriority = null
                    showInterstitialAdLog("priority Ad failed to show")
                    failListener?.invoke()
                    dismissLoadingDialog()
                    if (loadAgain) {
                        loadAgainPriorityInterstitialAd(activity, adIDLow)
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    isInterstitialAdOnScreen = true
                    showInterstitialAdLog("priority Ad successfully showed")
                    interstitialAdPriority = null
                    someOpPerformed = true
                    showListener?.invoke()
                    Handler().postDelayed({
                        dismissLoadingDialog()
                    }, 1000)
                }
            }

            someOpPerformed = false

            Handler().postDelayed({
                if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    it.show(activity)
                } else {
                    dismissLoadingDialog()
                }

            }, 1000)

        } ?: run {
            if (loadAgain) {
                loadAgainPriorityInterstitialAd(activity, adIDLow)
            }
        }
    }
}

private fun loadAgainPriorityInterstitialAd(
    activity: Activity,
    adIDLow: String?
) {
    if (adIDLow != null) {
        loadPriorityInterstitialAds(activity, adIDLow)
    }
}


/***************************************************************************
 * Normal Ad Flow
 * ***************************************************************************/

private fun showInterstitialAdLog(message: String) {
    Log.d(logTag, message)
}

private var loadingDialog: Dialog? = null
private fun showLoadingDialog(activity: Activity) {
    loadingDialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    loadingDialog?.setContentView(R.layout.dialog_resume_loading)
    loadingDialog?.setCancelable(false)
    try {
        if (loadingDialog?.isShowing == false) {
            loadingDialog?.show()
        }
    } catch (e: java.lang.IllegalArgumentException) {
    } catch (e: java.lang.Exception) {
    } catch (e: Exception) {
    }
}

private fun dismissLoadingDialog() {
    try {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    } catch (_: IllegalArgumentException) {
    }

}


}