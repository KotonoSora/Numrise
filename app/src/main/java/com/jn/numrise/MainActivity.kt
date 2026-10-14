package com.jn.numrise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import local.numrise.ui.di.AppContainerHost
import local.numrise.ui.di.AppViewModelFactory
import local.numrise.ui.di.LocalAppContainer
import local.numrise.ui.infrastructure.audio.LocalSoundManager
import local.numrise.ui.infrastructure.audio.SoundManager
import local.numrise.ui.presentation.MainApp
import local.numrise.ui.presentation.theme.NumriseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as AppContainerHost).container

        setContent {
            val soundManager = remember { SoundManager(applicationContext) }

            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            CompositionLocalProvider(
                LocalSoundManager provides soundManager,
                LocalAppContainer provides container
            ) {
                NumriseTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val factory = remember(container) { AppViewModelFactory(container) }
                        MainApp(factory = factory)
                    }
                }
            }
        }
    }
}
