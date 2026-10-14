package local.numrise.ui.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import local.numrise.ui.di.AppViewModelFactory
import local.numrise.ui.di.LocalAppContainer
import local.numrise.ui.infrastructure.audio.LocalSoundManager
import local.numrise.ui.presentation.navigation.NumriseNavGraph
import local.numrise.ui.presentation.viewmodel.GameViewModel
import local.numrise.ui.presentation.viewmodel.SettingsViewModel
import local.numrise.ui.presentation.viewmodel.ShopViewModel

@Composable
fun MainApp(factory: AppViewModelFactory) {
    val soundManager = LocalSoundManager.current
    // Sanctioned SFX-only wiring: the SFX toggle flows from core AudioSettings;
    // Settings routes toggles through core (see SettingsViewModel).
    val sfxEnabled by LocalAppContainer.current.audioSettings.sfxEnabledFlow
        .collectAsState(initial = true)
    soundManager.soundEnabled = sfxEnabled

    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val shopViewModel: ShopViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    // Drive legacy string sound events from GameViewModel through the SFX manager.
    androidx.compose.runtime.LaunchedEffect(gameViewModel) {
        gameViewModel.soundEvent.collect { name ->
            soundManager.play(name)
        }
    }

    NumriseNavGraph(
        gameViewModel = gameViewModel,
        shopViewModel = shopViewModel,
        settingsViewModel = settingsViewModel
    )
}
