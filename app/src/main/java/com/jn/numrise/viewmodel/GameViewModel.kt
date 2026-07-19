package com.jn.numrise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.domain.model.History
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.Tile
import com.jn.numrise.domain.repository.GameRepository
import com.jn.numrise.domain.usecase.FinishGameUseCase
import com.jn.numrise.domain.usecase.ProcessTileTapUseCase
import com.jn.numrise.domain.usecase.StartGameUseCase
import com.jn.numrise.domain.usecase.TapResult
import com.jn.numrise.domain.usecase.UpdatePlayerStatsUseCase
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
import kotlin.time.Duration.Companion.milliseconds

class GameViewModel(
    private val repository: GameRepository,
    private val startGameUseCase: StartGameUseCase,
    private val processTileTapUseCase: ProcessTileTapUseCase,
    private val finishGameUseCase: FinishGameUseCase,
    private val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _soundEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val soundEvent: SharedFlow<String> = _soundEvent.asSharedFlow()

    private var timerJob: Job? = null
    private val tappedHistory = mutableListOf<Tile>()

    init {
        observePlayerStats()
        observeLevels()
        observeHistory()
    }

    private fun observePlayerStats() {
        viewModelScope.launch {
            repository.getPlayerStats().collectLatest { stats ->
                _uiState.update {
                    it.copy(
                        coins = stats.coins,
                        soundEnabled = stats.soundEnabled
                    )
                }
            }
        }
    }

    private fun observeLevels() {
        viewModelScope.launch {
            repository.getAllLevels().collectLatest { levels ->
                _uiState.update { it.copy(levels = levels) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getAllHistory().collectLatest { history ->
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
            GameIntent.BuyExtraTime -> useExtraTime()
            GameIntent.ResetToIdle -> resetToIdle()
            is GameIntent.BuyCoins -> buyCoins(intent.amount)
            is GameIntent.SetSoundEnabled -> setSoundEnabled(intent.enabled)
            GameIntent.StartDailyChallenge -> startDailyChallenge()
            GameIntent.StartNext -> startNext()
        }
    }

    private fun startNext() {
        val currentState = _uiState.value
        // Synchronously reset status to IDLE and clear tiles to prevent GamePlayScreen from 
        // immediately navigating back to ResultScreen or showing a stale board.
        _uiState.update { it.copy(status = GameStatus.IDLE, tiles = emptyList()) }

        when {
            currentState.currentLevel != null -> {
                val level = currentState.currentLevel
                viewModelScope.launch {
                    val nextLevel = repository.getLevelById(level.id + 1)
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
                // If neither level nor difficulty (e.g. Daily Challenge), restart what was there
                startDailyChallenge()
            }
        }
    }

    private fun startDailyChallenge() {
        val initialState = startGameUseCase.executeDailyChallenge()
        tappedHistory.clear()
        _uiState.update {
            it.copy(
                tiles = initialState.tiles,
                gridSize = initialState.gridSize,
                currentTarget = initialState.currentTarget,
                timerSeconds = initialState.timerSeconds,
                status = initialState.status,
                score = 0,
                currentDifficulty = null,
                currentLevel = null,
                highlightedTileId = null
            )
        }
        startCountdown()
    }

    private fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updatePlayerStatsUseCase.setSoundEnabled(enabled)
        }
    }

    private fun startGame(difficulty: Difficulty) {
        val initialState = startGameUseCase.execute(difficulty)
        tappedHistory.clear()
        _uiState.update {
            it.copy(
                tiles = initialState.tiles,
                gridSize = initialState.gridSize,
                currentTarget = initialState.currentTarget,
                timerSeconds = initialState.timerSeconds,
                status = initialState.status,
                score = 0,
                currentDifficulty = initialState.currentDifficulty,
                currentLevel = null,
                highlightedTileId = null
            )
        }
        startCountdown()
    }

    private fun startGame(level: Level) {
        val initialState = startGameUseCase.execute(level)
        tappedHistory.clear()
        _uiState.update {
            it.copy(
                tiles = initialState.tiles,
                gridSize = initialState.gridSize,
                currentTarget = initialState.currentTarget,
                timerSeconds = initialState.timerSeconds,
                status = initialState.status,
                score = 0,
                currentDifficulty = null,
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

    private fun failGame() {
        timerJob?.cancel()
        viewModelScope.launch { _soundEvent.emit("error") }
        val currentState = _uiState.value
        _uiState.update { it.copy(status = GameStatus.FAILED, lastReward = 0) }

        saveHistory(
            score = currentState.score,
            reward = 0,
            isWin = false
        )
    }

    private fun saveHistory(score: Int, reward: Int, isWin: Boolean) {
        val currentState = _uiState.value
        viewModelScope.launch {
            repository.saveHistory(
                History(
                    date = System.currentTimeMillis(),
                    score = score,
                    reward = reward,
                    levelId = currentState.currentLevel?.id,
                    difficulty = currentState.currentDifficulty?.name,
                    isWin = isWin
                )
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

        saveHistory(result.score, result.reward, true)

        viewModelScope.launch {
            currentState.currentLevel?.let { level ->
                updatePlayerStatsUseCase.updateGameStats(
                    level = level,
                    score = result.score,
                    timeElapsed = result.timeElapsed,
                    stars = result.stars,
                    rewardCoins = result.reward
                )
            } ?: run {
                // Award coins for difficulty mode
                updatePlayerStatsUseCase.addCoins(result.reward)
            }
        }
    }

    private fun useHint() {
        val currentState = _uiState.value
        if (currentState.status == GameStatus.PLAYING && currentState.coins >= 50) {
            val targetTile = currentState.tiles.find { it.number == currentState.currentTarget }
            if (targetTile != null) {
                viewModelScope.launch {
                    if (updatePlayerStatsUseCase.spendCoins(50)) {
                        _uiState.update { it.copy(highlightedTileId = targetTile.id) }
                    }
                }
            }
        }
    }

    private fun useUndo() {
        val currentState = _uiState.value
        if (currentState.status == GameStatus.PLAYING && currentState.coins >= 50 && tappedHistory.isNotEmpty()) {
            viewModelScope.launch {
                if (updatePlayerStatsUseCase.spendCoins(50)) {
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
        }
    }

    private fun useExtraTime() {
        val currentState = _uiState.value
        if (currentState.status == GameStatus.PLAYING && currentState.coins >= 30) {
            viewModelScope.launch {
                if (updatePlayerStatsUseCase.spendCoins(30)) {
                    _uiState.update { it.copy(timerSeconds = it.timerSeconds + 60) }
                }
            }
        }
    }

    private fun buyCoins(amount: Int) {
        viewModelScope.launch {
            updatePlayerStatsUseCase.addCoins(amount)
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

}
