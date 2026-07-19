package com.jn.numrise

import android.app.Application
import com.jn.numrise.di.AppContainer

class NumriseApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
