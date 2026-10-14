package local.numrise.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.numrise.engine.application.FinishGameUseCase
import local.numrise.engine.application.GameInitialState
import local.numrise.engine.application.ProcessTileTapUseCase
import local.numrise.engine.application.StartGameUseCase
import local.numrise.engine.application.TapResult
import local.numrise.engine.domain.model.Difficulty
import local.numrise.engine.domain.model.GameStatus
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.model.RunEndCause
import local.numrise.engine.domain.model.RunRecord
import local.numrise.engine.domain.model.Tile
import local.numrise.engine.domain.repository.LevelRepository
import local.numrise.engine.domain.repository.RunHistoryRepository
import local.numrise.shop.domain.model.BoostIds
import local.numrise.shop.domain.service.Boosts
import local.numrise.ui.application.RecordGameFinishedUseCase
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(
    private val coinAccount: CoinAccount,
    private val audioSettings: AudioSettings,
    private val levelRepository: LevelRepository,
    private val runHistoryRepository: RunHistoryRepository,
    private val startGameUseCase: StartGameUseCase,
    private val processTileTapUseCase: ProcessTileTapUseCase,
    private val finishGameUseCase: FinishGameUseCase,
    private val recordGameFinishedUseCase: RecordGameFinishedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _soundEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val soundEvent: SharedFlow<String> = _soundEvent.asSharedFlow()

    private var timerJob: Job? = null
    private val tappedHistory = mutableListOf<Tile>()

    private var sessionStartedAt: Long = System.currentTimeMillis()
    private var sessionHintUses = 0
    private var sessionUndoUses = 0
    private var sessionExtraTimeUses = 0

    init {
        observeCoins()
        observeSound()
        observeLevels()
        observeHistory()
    }

    private fun observeCoins() {
        viewModelScope.launch {
            coinAccount.coinsFlow.collectLatest { coins ->
                _uiState.update { it.copy(coins = coins) }
            }
        }
    }

    private fun observeSound() {
        viewModelScope.launch {
            audioSettings.sfxEnabledFlow.collectLatest { enabled ->
                _uiState.update { it.copy(soundEnabled = enabled) }
            }
        }
    }

    private fun observeLevels() {
        viewModelScope.launch {
            levelRepository.levelsFlow().collectLatest { levels ->
                _uiState.update { it.copy(levels = levels) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            runHistoryRepository.recentRunsFlow(50).collectLatest { history ->
                _uiState.update { it.copy(history = history) }
            }
        }
    }

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartWithDifficulty -> startGame(intent.difficulty)
            is GameIntent.StartWithLevel -> startGame(intent.level)
            is GameIntent.TileTapped -> onTileTapped(intent.tile)
            GameIntent.RestartGame -> restartGame()
            GameIntent.UseHint -> useHint()
            GameIntent.UseUndo -> useUndo()
            GameIntent.UseExtraTime -> useExtraTime()
            GameIntent.ResetToIdle -> resetToIdle()
            GameIntent.StartDailyChallenge -> startDailyChallenge()
            GameIntent.StartNext -> startNext()
        }
    }

    private fun resetSessionCounters() {
        sessionStartedAt = System.currentTimeMillis()
        sessionHintUses = 0
        sessionUndoUses = 0
        sessionExtraTimeUses = 0
    }

    private fun startNext() {
        val currentState = _uiState.value
        _uiState.update { it.copy(status = GameStatus.IDLE, tiles = emptyList()) }

        when {
            currentState.currentLevel != null -> {
                val level = currentState.currentLevel
                viewModelScope.launch {
                    val nextLevel = levelRepository.getLevelById(level.id + 1)
                    if (nextLevel != null && nextLevel.isUnlocked) {
                        startGame(nextLevel)
                    } else {
                        startGame(level)
                    }
                }
            }

            currentState.currentDifficulty != null -> {
                val difficulty = currentState.currentDifficulty
                val nextDifficulty = Difficulty.entries.getOrNull(difficulty.ordinal + 1)
                if (nextDifficulty != null) {
                    startGame(nextDifficulty)
                } else {
                    startGame(difficulty)
                }
            }

            else -> {
                startDailyChallenge()
            }
        }
    }

    private fun startDailyChallenge() {
        launchGame(startGameUseCase.executeDailyChallenge())
    }

    private fun startGame(difficulty: Difficulty) {
        launchGame(startGameUseCase.execute(difficulty))
    }

    private fun startGame(level: Level) {
        launchGame(startGameUseCase.execute(level))
    }

    /**
     * Single entry for every new run: clears tap history + per-run counters,
     * publishes the engine initial state, restarts the countdown.
     */
    private fun launchGame(initialState: GameInitialState) {
        tappedHistory.clear()
        resetSessionCounters()
        _uiState.update {
            it.copy(
                tiles = initialState.tiles,
                gridSize = initialState.gridSize,
                currentTarget = initialState.currentTarget,
                timerSeconds = initialState.timerSeconds,
                status = initialState.status,
                score = 0,
                currentDifficulty = initialState.currentDifficulty,
                currentLevel = initialState.currentLevel,
                highlightedTileId = null
            )
        }
        startCountdown()
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000.milliseconds)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            if (_uiState.value.status == GameStatus.PLAYING) {
                failGame()
            }
        }
    }

    private fun modeFor(state: GameUiState): String =
        state.currentDifficulty?.name
            ?: state.currentLevel?.let { "LEVEL_${it.id}" }
            ?: "DAILY"

    /**
     * Single run-record builder for win + fail paths. Repurposed core fields
     * stay mapped here only: `pipesPassed` = tapped-tile count, `shieldUses`
     * = hint uses, `multiplierUses` = undo uses, `autoPlayUses` = extra-time
     * uses (see engine `RunRecord` KDoc, Option A — no schema change).
     */
    private fun buildRunRecord(
        state: GameUiState,
        score: Int,
        reward: Int,
        tappedCount: Int,
        endCause: RunEndCause,
        endedAt: Long
    ): RunRecord = RunRecord(
        mode = modeFor(state),
        level = state.gridSize,
        score = score,
        reward = reward,
        durationMs = endedAt - sessionStartedAt,
        pipesPassed = tappedCount,
        endCause = endCause,
        shieldUses = sessionHintUses,
        multiplierUses = sessionUndoUses,
        autoPlayUses = sessionExtraTimeUses,
        startedAt = sessionStartedAt,
        endedAt = endedAt
    )

    private fun failGame() {
        timerJob?.cancel()
        viewModelScope.launch { _soundEvent.emit("error") }
        val currentState = _uiState.value
        val endedAt = System.currentTimeMillis()
        _uiState.update { it.copy(status = GameStatus.FAILED, lastReward = 0) }

        viewModelScope.launch {
            recordGameFinishedUseCase(
                buildRunRecord(
                    state = currentState,
                    score = currentState.score,
                    reward = 0,
                    tappedCount = currentState.tiles.count { it.isTapped },
                    endCause = RunEndCause.GAME_OVER,
                    endedAt = endedAt
                ),
                level = currentState.currentLevel,
                score = currentState.score,
                timeElapsed = 0,
                stars = 0,
                isWin = false
            )
        }
    }

    private fun onTileTapped(tile: Tile) {
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING) return

        val result = processTileTapUseCase.execute(
            tile = tile,
            currentTarget = currentState.currentTarget,
            currentTiles = currentState.tiles,
            currentScore = currentState.score
        )

        when (result) {
            is TapResult.Correct -> {
                viewModelScope.launch { _soundEvent.emit("tap") }
                tappedHistory.add(tile)
                _uiState.update {
                    it.copy(
                        tiles = result.updatedTiles,
                        currentTarget = result.nextTarget,
                        score = result.newScore,
                        highlightedTileId = null
                    )
                }
            }

            is TapResult.Incorrect -> {
                viewModelScope.launch { _soundEvent.emit("error") }
                _uiState.update { it.copy(score = result.newScore) }
            }

            is TapResult.Finished -> {
                finishGame(result.finalTiles)
            }
        }
    }

    private fun finishGame(finalTiles: List<Tile>) {
        timerJob?.cancel()
        viewModelScope.launch { _soundEvent.emit("win") }
        val currentState = _uiState.value
        val endedAt = System.currentTimeMillis()

        val result = finishGameUseCase.execute(
            currentScore = currentState.score,
            timerSeconds = currentState.timerSeconds,
            currentDifficulty = currentState.currentDifficulty,
            currentLevel = currentState.currentLevel,
            finalTiles = finalTiles
        )

        _uiState.update {
            it.copy(
                tiles = result.finalTiles,
                status = GameStatus.FINISHED,
                score = result.score,
                lastReward = result.reward
            )
        }

        viewModelScope.launch {
            recordGameFinishedUseCase(
                buildRunRecord(
                    state = currentState,
                    score = result.score,
                    reward = result.reward,
                    tappedCount = result.finalTiles.count { it.isTapped },
                    endCause = RunEndCause.VICTORY,
                    endedAt = endedAt
                ),
                level = currentState.currentLevel,
                score = result.score,
                timeElapsed = result.timeElapsed,
                stars = result.stars
            )
        }
    }

    /**
     * Single coin-gated spend path for all boosts. Resolves the cost from the
     * `:numrise-shop` catalog ([Boosts.costFor]), guards game state + balance,
     * spends, then runs the boost effect only on success. Callers keep only
     * their no-op guards (e.g. hint with no target, undo with empty history).
     */
    private fun spendBoost(boostId: String, extraGuard: Boolean = true, onSpent: () -> Unit) {
        val cost = Boosts.costFor(boostId) ?: return
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING || !extraGuard || currentState.coins < cost) return
        viewModelScope.launch {
            if (coinAccount.spendCoins(cost)) onSpent()
        }
    }

    private fun useHint() {
        val currentState = _uiState.value
        val targetTile = currentState.tiles.find { it.number == currentState.currentTarget }
            ?: return
        spendBoost(BoostIds.BOOST_HINT_ID) {
            sessionHintUses++
            _uiState.update { it.copy(highlightedTileId = targetTile.id) }
        }
    }

    private fun useUndo() {
        spendBoost(BoostIds.BOOST_UNDO_ID, extraGuard = tappedHistory.isNotEmpty()) {
            sessionUndoUses++
            val currentState = _uiState.value
            val lastTile = tappedHistory.removeAt(tappedHistory.size - 1)
            val updatedTiles = currentState.tiles.map {
                if (it.id == lastTile.id) it.copy(isTapped = false) else it
            }
            _uiState.update {
                it.copy(
                    tiles = updatedTiles,
                    currentTarget = currentState.currentTarget - 1,
                    score = maxOf(0, it.score - 100)
                )
            }
        }
    }

    private fun useExtraTime() {
        spendBoost(BoostIds.BOOST_EXTRA_TIME_ID) {
            sessionExtraTimeUses++
            _uiState.update { it.copy(timerSeconds = it.timerSeconds + Boosts.EXTRA_TIME_SECONDS) }
        }
    }

    private fun restartGame() {
        val state = _uiState.value
        state.currentDifficulty?.let { startGame(it) }
            ?: state.currentLevel?.let { startGame(it) }
    }

    private fun resetToIdle() {
        _uiState.update { current ->
            GameUiState(
                coins = current.coins,
                soundEnabled = current.soundEnabled,
                levels = current.levels,
                history = current.history
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
