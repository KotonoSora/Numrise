package com.kotonosora.numrise

import android.app.Application
import com.kotonosora.numrise.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NumriseApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val levelDao by lazy { database.levelDao() }
}
