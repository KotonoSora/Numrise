package com.jn.numrise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jn.numrise.ui.navigation.NumriseNavGraph
import com.jn.numrise.ui.theme.NumriseTheme
import com.jn.numrise.viewmodel.GameViewModel
import com.jn.numrise.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            val container = (application as NumriseApplication).container
            val gameViewModel: GameViewModel by viewModels {
                GameViewModelFactory(container)
            }

            val soundManager = container.soundManager
            val uiState by gameViewModel.uiState.collectAsState()
            val soundEnabled = uiState.soundEnabled

            LaunchedEffect(soundEnabled) {
                gameViewModel.soundEvent.collectLatest { soundName ->
                    if (soundEnabled) {
                        soundManager.play(soundName)
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    // soundManager is managed by AppContainer now
                }
            }

            NumriseTheme {
                NumriseNavGraph(gameViewModel = gameViewModel)
            }
        }
    }
}
