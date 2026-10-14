package local.numrise.ui.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import local.core.billing.BillingConfig
import local.core.billing.BillingManagerImpl
import local.core.billing.BillingRepository
import local.core.billing.MockBillingManager
import local.core.shop.coins.CoinPackIds
import local.core.shop.coins.CoinPacks
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.core.ui.data.RoomRunStore
import local.core.ui.data.RunStore
import local.core.ui.data.SettingsRepositoryImpl
import local.core.ui.data.db.createGameDatabase
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.GetCoinsUseCase
import local.core.ui.usecase.ToggleAudioUseCase
import local.numrise.engine.application.FinishGameUseCase
import local.numrise.engine.application.ProcessTileTapUseCase
import local.numrise.engine.application.StartGameUseCase
import local.numrise.engine.domain.repository.LevelRepository
import local.numrise.engine.domain.repository.RunHistoryRepository
import local.numrise.shop.domain.model.SkinIds
import local.numrise.shop.infrastructure.persistence.NumriseInventoryKeys
import local.numrise.ui.adapters.asCoinSink
import local.numrise.ui.adapters.asRunHistoryRepository
import local.numrise.ui.application.RecordGameFinishedUseCase
import local.numrise.ui.infrastructure.persistence.LevelDatabase
import local.numrise.ui.infrastructure.persistence.RoomLevelRepository

/**
 * Dependency Injection container for the application. Manual DI, no Hilt/Koin.
 */
interface AppContainer {
    val coinAccount: CoinAccount
    val audioSettings: AudioSettings
    val levelRepository: LevelRepository
    val runHistoryRepository: RunHistoryRepository
    val billingRepository: BillingRepository

    val startGameUseCase: StartGameUseCase
    val processTileTapUseCase: ProcessTileTapUseCase
    val finishGameUseCase: FinishGameUseCase
    val getCoinsUseCase: GetCoinsUseCase
    val getAudioSettingsUseCase: GetAudioSettingsUseCase
    val toggleAudioUseCase: ToggleAudioUseCase
    val recordGameFinishedUseCase: RecordGameFinishedUseCase
}

/**
 * Default implementation of the [AppContainer] interface.
 */
class DefaultAppContainer(
    private val context: Context,
    private val isDebug: Boolean
) : AppContainer {

    private val applicationScope = CoroutineScope(SupervisorJob())

    /**
     * Single DataStore-backed store, exposed through narrow ports so each
     * consumer depends only on what it uses. The Numrise inventory map lives in
     * `:numrise-shop` and is injected via constructor (core stays game-agnostic).
     * Coin-direct variant: nothing reads inventory keys, but the wiring is kept
     * so existing installs keep their data.
     */
    private val settingsStore: SettingsRepositoryImpl by lazy {
        SettingsRepositoryImpl(
            context,
            SkinIds.SKIN_DEFAULT_ID,
            NumriseInventoryKeys.map()
        )
    }

    override val coinAccount: CoinAccount by lazy { settingsStore }
    override val audioSettings: AudioSettings by lazy { settingsStore }

    override val levelRepository: LevelRepository by lazy {
        RoomLevelRepository(LevelDatabase.getDatabase(context, applicationScope).levelDao())
    }

    private val runStore: RunStore by lazy {
        RoomRunStore(createGameDatabase(context).runDao())
    }

    override val runHistoryRepository: RunHistoryRepository by lazy {
        runStore.asRunHistoryRepository()
    }

    override val billingRepository: BillingRepository by lazy {
        val wallet = coinAccount.asCoinSink()
        if (isDebug) {
            MockBillingManager(wallet, CoinPacks.mockProducts, CoinPacks::getCoinAmount)
        } else {
            BillingManagerImpl(
                context,
                wallet,
                BillingConfig(CoinPackIds.ALL, CoinPacks::getCoinAmount)
            )
        }
    }

    override val startGameUseCase: StartGameUseCase by lazy { StartGameUseCase() }
    override val processTileTapUseCase: ProcessTileTapUseCase by lazy { ProcessTileTapUseCase() }
    override val finishGameUseCase: FinishGameUseCase by lazy { FinishGameUseCase() }

    override val getAudioSettingsUseCase: GetAudioSettingsUseCase by lazy {
        GetAudioSettingsUseCase(audioSettings)
    }
    override val getCoinsUseCase: GetCoinsUseCase by lazy {
        GetCoinsUseCase(coinAccount)
    }
    override val toggleAudioUseCase: ToggleAudioUseCase by lazy {
        ToggleAudioUseCase(audioSettings)
    }
    override val recordGameFinishedUseCase: RecordGameFinishedUseCase by lazy {
        RecordGameFinishedUseCase(coinAccount, runHistoryRepository, levelRepository)
    }
}
