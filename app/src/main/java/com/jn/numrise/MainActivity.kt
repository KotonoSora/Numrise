package com.jn.numrise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jn.numrise.audio.LocalSoundManager
import com.jn.numrise.ui.navigation.NumriseNavGraph
import com.jn.numrise.ui.theme.NumriseTheme
import com.jn.numrise.viewmodel.GameViewModel
import com.jn.numrise.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as NumriseApplication).container

        setContent {
            NumriseTheme {
                CompositionLocalProvider(LocalSoundManager provides container.soundManager) {
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel(
                        factory = GameViewModelFactory(container)
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { innerPadding ->
                        NumriseNavGraph(
                            navController = navController,
                            contentPadding = innerPadding,
                            gameViewModel = gameViewModel
                        )
                    }
                }
            }
        }
    }
}
