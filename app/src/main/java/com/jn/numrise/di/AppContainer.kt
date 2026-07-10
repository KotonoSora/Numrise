package com.jn.numrise.di

import android.content.Context
import com.jn.numrise.audio.SoundManager
import com.jn.numrise.billing.BillingManager
import com.jn.numrise.data.AppDatabase
import com.jn.numrise.data.repository.GameRepositoryImpl
import com.jn.numrise.domain.repository.GameRepository
import com.jn.numrise.domain.usecase.ProcessTileTapUseCase
import com.jn.numrise.domain.usecase.StartGameUseCase
import com.jn.numrise.domain.usecase.UpdatePlayerStatsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppContainer(private val context: Context) {

    val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy {
        AppDatabase.getDatabase(context, applicationScope)
    }

    val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(database.levelDao())
    }

    val soundManager: SoundManager by lazy {
        SoundManager(context)
    }

    val billingManager: BillingManager by lazy {
        BillingManager(context, applicationScope, gameRepository)
    }

    // Use Cases
    val startGameUseCase: StartGameUseCase by lazy {
        StartGameUseCase()
    }

    val processTileTapUseCase: ProcessTileTapUseCase by lazy {
        ProcessTileTapUseCase()
    }

    val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase by lazy {
        UpdatePlayerStatsUseCase(gameRepository)
    }
}
