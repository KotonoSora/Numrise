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
import com.jn.numrise.audio.SoundManager
import com.jn.numrise.ui.navigation.NumriseNavGraph
import com.jn.numrise.ui.theme.NumriseTheme
import com.jn.numrise.viewmodel.GameViewModel
import com.jn.numrise.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    private var soundManager: SoundManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        soundManager = SoundManager(this)

        setContent {
            NumriseTheme {
                CompositionLocalProvider(LocalSoundManager provides soundManager) {
                    val navController = rememberNavController()
                    val app = (application as NumriseApplication)
                    val gameViewModel: GameViewModel = viewModel(
                        factory = GameViewModelFactory(app.levelDao)
                    )

                    // Inject soundManager into ViewModel
                    gameViewModel.soundManager = soundManager

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

    override fun onDestroy() {
        super.onDestroy()
        soundManager?.release()
    }
}
