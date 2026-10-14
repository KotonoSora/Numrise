package local.numrise.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.ToggleAudioUseCase

data class AudioState(
    val soundEnabled: Boolean = true
)

/**
 * SFX-only settings. The game has no BGM: only `sfxEnabledFlow` is exposed.
 * Core still persists `music_enabled` in `user_settings` for cross-game reuse,
 * but nothing in this game reads or toggles it.
 */
class SettingsViewModel(
    getAudioSettings: GetAudioSettingsUseCase,
    private val toggleAudioUseCase: ToggleAudioUseCase
) : ViewModel() {

    val settings: StateFlow<AudioState> = getAudioSettings.sfxEnabledFlow
        .map { sound -> AudioState(soundEnabled = sound) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AudioState())

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            toggleAudioUseCase.toggleSfx(enabled)
        }
    }
}
