package com.phone.tracker.locate.number.app.MyApp

import com.phone.tracker.locate.number.app.repo.ContactsRepo
import com.number.locator.phone.tracker.app.ui.viewModels.LocationViewModel
import com.number.locator.phone.tracker.app.utills.AppPreferences
import org.koin.dsl.module


object AppModule {

    val getModule = module {
//
        single { ContactsRepo(context = get()) }
        single { AppPreferences(context = get()) }
        single { LocationViewModel(application = get()) }


    }
}