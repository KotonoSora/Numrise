package com.jn.numrise

import android.app.Application
import local.numrise.ui.di.AppContainer
import local.numrise.ui.di.AppContainerHost
import local.numrise.ui.di.DefaultAppContainer

class MainApplication : Application(), AppContainerHost {
    override lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this, BuildConfig.DEBUG)
    }
}
