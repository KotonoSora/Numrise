package com.jn.numrise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.numrise.audio.SoundManager
import com.jn.numrise.domain.mapper.GameMapper
import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.Tile
import com.jn.numrise.domain.repository.GameRepository
import com.jn.numrise.domain.usecase.ProcessTileTapUseCase
import com.jn.numrise.domain.usecase.StartGameUseCase
import com.jn.numrise.domain.usecase.TapResult
import com.jn.numrise.domain.usecase.UpdatePlayerStatsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository,
    private val startGameUseCase: StartGameUseCase,
    private val processTileTapUseCase: ProcessTileTapUseCase,
    private val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val tappedHistory = mutableListOf<Tile>()

    init {
        observePlayerStats()
        observeLevels()
    }

    private fun observePlayerStats() {
        viewModelScope.launch {
            repository.getPlayerStats().collectLatest { stats ->
                _uiState.update { it.copy(coins = stats?.coins ?: 0) }
            }
        }
    }

    private fun observeLevels() {
        viewModelScope.launch {
            repository.getAllLevels().collectLatest { entities ->
                val domainLevels = entities.map { GameMapper.mapToLevel(it) }
                _uiState.update { it.copy(levels = domainLevels) }
            }
        }
    }

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartWithDifficulty -> startGame(intent.difficulty)
            is GameIntent.StartWithLevel -> startGame(intent.level)
            is GameIntent.TileTapped -> onTileTapped(intent.tile)
            GameIntent.PauseGame -> pauseGame()
            GameIntent.ResumeGame -> resumeGame()
            GameIntent.RestartGame -> restartGame()
            GameIntent.UseHint -> useHint()
            GameIntent.UseUndo -> useUndo()
            GameIntent.ResetToIdle -> resetToIdle()
            is GameIntent.BuyCoins -> buyCoins(intent.amount)
            is GameIntent.SetSoundEnabled -> setSoundEnabled(intent.enabled)
        }
    }

    private fun setSoundEnabled(enabled: Boolean) {
        soundManager.isEnabled = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
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
                delay(1000)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            if (_uiState.value.status == GameStatus.PLAYING) {
                failGame()
            }
        }
    }

    private fun failGame() {
        timerJob?.cancel()
        soundManager.play("error")
        _uiState.update { it.copy(status = GameStatus.FAILED) }
    }

    private fun pauseGame() {
        timerJob?.cancel()
        _uiState.update { it.copy(status = GameStatus.PAUSED) }
    }

    private fun resumeGame() {
        if (_uiState.value.status == GameStatus.PAUSED) {
            _uiState.update { it.copy(status = GameStatus.PLAYING) }
            startCountdown()
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
                soundManager.play("tap")
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
                soundManager.play("error")
                _uiState.update { it.copy(score = result.newScore) }
            }
            is TapResult.Finished -> {
                finishGame(result.finalTiles)
            }
        }
    }

    private fun finishGame(finalTiles: List<Tile>) {
        timerJob?.cancel()
        soundManager.play("win")
        val currentState = _uiState.value

        val timeLimit = currentState.currentDifficulty?.timeLimit ?: 60
        val timeElapsed = timeLimit - currentState.timerSeconds
        val timeBonus = currentState.timerSeconds * 10
        val finalScore = currentState.score + timeBonus

        val stars = when {
            currentState.timerSeconds > timeLimit * 0.6 -> 3
            currentState.timerSeconds > timeLimit * 0.3 -> 2
            else -> 1
        }

        _uiState.update {
            it.copy(
                tiles = finalTiles,
                status = GameStatus.FINISHED,
                score = finalScore
            )
        }

        viewModelScope.launch {
            currentState.currentLevel?.let { level ->
                updatePlayerStatsUseCase.updateGameStats(
                    level = level,
                    score = finalScore,
                    timeElapsed = timeElapsed,
                    stars = stars
                )
            } ?: run {
                // Award coins for difficulty mode
                updatePlayerStatsUseCase.addCoins(10 + (stars * 5))
            }
        }
    }

    private fun useHint() {
        val currentState = _uiState.value
        if (currentState.status == GameStatus.PLAYING && currentState.coins >= 10) {
            val targetTile = currentState.tiles.find { it.number == currentState.currentTarget }
            if (targetTile != null) {
                viewModelScope.launch {
                    if (updatePlayerStatsUseCase.spendCoins(10)) {
                        _uiState.update { it.copy(highlightedTileId = targetTile.id) }
                    }
                }
            }
        }
    }

    private fun useUndo() {
        val currentState = _uiState.value
        if (currentState.status == GameStatus.PLAYING && currentState.coins >= 5 && tappedHistory.isNotEmpty()) {
            viewModelScope.launch {
                if (updatePlayerStatsUseCase.spendCoins(5)) {
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
        _uiState.update { GameUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
