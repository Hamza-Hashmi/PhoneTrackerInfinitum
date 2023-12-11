package com.number.locator.phone.tracker.app.MyApp

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.number.locator.phone.tracker.app.googleAds.AppOpenClass
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.number.locator.phone.tracker.app.googleAds.loadAndReturnAd
import com.phone.tracker.locate.number.app.MyApp.AppModule
import com.phone.tracker.locate.number.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppClass : Application() {



      var appOpenManager: AppOpenClass?=null
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@AppClass)
            modules(AppModule.getModule)
        }
        MobileAds.initialize(this@AppClass)
        appOpenManager= AppOpenClass(this@AppClass)



    }
}


fun Application.loadAppOpen(){
    if(this is AppClass){
        appOpenManager?.fetchAd()
    }
}