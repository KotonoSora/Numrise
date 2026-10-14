package local.numrise.ui.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import local.numrise.ui.presentation.viewmodel.GameViewModel
import local.numrise.ui.presentation.viewmodel.SettingsViewModel
import local.numrise.ui.presentation.viewmodel.ShopViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    container.coinAccount,
                    container.audioSettings,
                    container.levelRepository,
                    container.runHistoryRepository,
                    container.startGameUseCase,
                    container.processTileTapUseCase,
                    container.finishGameUseCase,
                    container.recordGameFinishedUseCase
                ) as T
            }

            modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                ShopViewModel(
                    container.billingRepository,
                    container.getCoinsUseCase
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    container.getAudioSettingsUseCase,
                    container.toggleAudioUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
