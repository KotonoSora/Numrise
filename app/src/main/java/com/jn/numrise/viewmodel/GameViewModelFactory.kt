package com.jn.numrise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jn.numrise.di.AppContainer

class GameViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    repository = container.gameRepository,
                    startGameUseCase = container.startGameUseCase,
                    processTileTapUseCase = container.processTileTapUseCase,
                    updatePlayerStatsUseCase = container.updatePlayerStatsUseCase
                ) as T
            }

            modelClass.isAssignableFrom(CoinShopViewModel::class.java) -> {
                CoinShopViewModel(
                    billingManager = container.billingManager,
                    repository = container.gameRepository,
                    updatePlayerStatsUseCase = container.updatePlayerStatsUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
