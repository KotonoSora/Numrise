package com.jn.numrise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.numrise.audio.SoundManager
import com.jn.numrise.data.LevelDao
import com.jn.numrise.data.LevelEntity
import com.jn.numrise.data.PlayerStatsEntity
import com.jn.numrise.model.Difficulty
import com.jn.numrise.model.GameStatus
import com.jn.numrise.model.Tile
import com.jn.numrise.model.getTileColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class GameViewModel(private val levelDao: LevelDao, var soundManager: SoundManager? = null) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val allLevels: StateFlow<List<LevelEntity>> = levelDao.getAllLevels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playerStats: StateFlow<PlayerStatsEntity?> = levelDao.getPlayerStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerStatsEntity())

    private var timerJob: Job? = null
    private val tappedHistory = mutableListOf<Tile>()

    fun startGame(difficulty: Difficulty) {
        val numbers = (1..difficulty.maxNumber).shuffled()
        val colors = getTileColors()
        
        val tiles = numbers.mapIndexed { index, number ->
            Tile(
                id = index,
                number = number,
                color = colors[index % colors.size]
            )
        }

        val gridSize = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 4
            Difficulty.HARD -> 5
            Difficulty.VERY_HARD -> 6
        }

        tappedHistory.clear()
        _uiState.update {
            it.copy(
                tiles = tiles,
                gridSize = gridSize,
                currentTarget = 1,
                timerSeconds = difficulty.timeLimit,
                status = GameStatus.PLAYING,
                score = 0,
                currentDifficulty = difficulty,
                highlightedTileId = null
            )
        }
        startCountdown()
    }

    // Keep original startGame for LevelEntity if still used by LevelSelectScreen
    fun startGame(level: LevelEntity) {
        val totalTiles = level.gridSize * level.gridSize
        val numbers = (1..totalTiles).shuffled()
        val colors = getTileColors()
        
        val tiles = numbers.mapIndexed { index, number ->
            Tile(
                id = index,
                number = number,
                color = colors[index % colors.size]
            )
        }

        tappedHistory.clear()
        _uiState.update {
            it.copy(
                tiles = tiles,
                gridSize = level.gridSize,
                currentTarget = 1,
                timerSeconds = 60, // Default for level entity
                status = GameStatus.PLAYING,
                score = 0,
                currentLevelEntity = level,
                currentDifficulty = null,
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
        soundManager?.play("error")
        _uiState.update { it.copy(status = GameStatus.FAILED) }
    }

    fun pauseGame() {
        timerJob?.cancel()
        _uiState.update { it.copy(status = GameStatus.PAUSED) }
    }

    fun resumeGame() {
        if (_uiState.value.status == GameStatus.PAUSED) {
            _uiState.update { it.copy(status = GameStatus.PLAYING) }
            startCountdown()
        }
    }

    fun onTileTapped(tile: Tile) {
        val currentState = _uiState.value
        if (currentState.status != GameStatus.PLAYING) return

        if (tile.number == currentState.currentTarget) {
            // Correct tap
            soundManager?.play("tap")
            tappedHistory.add(tile)
            val updatedTiles = currentState.tiles.map {
                if (it.id == tile.id) it.copy(isTapped = true) else it
            }
            
            val nextTarget = currentState.currentTarget + 1
            val isFinished = nextTarget > currentState.tiles.size

            if (isFinished) {
                finishGame(updatedTiles)
            } else {
                _uiState.update {
                    it.copy(
                        tiles = updatedTiles,
                        currentTarget = nextTarget,
                        score = it.score + 100,
                        highlightedTileId = null // Clear hint on correct tap
                    )
                }
            }
        } else {
            // Incorrect tap penalty
            soundManager?.play("error")
            _uiState.update { it.copy(score = maxOf(0, it.score - 50)) }
        }
    }

    private fun finishGame(finalTiles: List<Tile>) {
        timerJob?.cancel()
        soundManager?.play("win")
        val currentState = _uiState.value
        
        // Calculate bonus and stars
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
            currentState.currentLevelEntity?.let { level ->
                // Update level stats
                val updatedLevel = level.copy(
                    highScore = maxOf(level.highScore, finalScore),
                    bestTimeSeconds = minOf(level.bestTimeSeconds, timeElapsed),
                    stars = maxOf(level.stars, stars)
                )
                levelDao.updateLevel(updatedLevel)

                // Unlock next level
                levelDao.getLevelById(level.id + 1)?.let { nextLevel ->
                    if (!nextLevel.isUnlocked) {
                        levelDao.updateLevel(nextLevel.copy(isUnlocked = true))
                    }
                }
            }

            // Award coins
            val currentCoins = playerStats.value?.coins ?: 0
            val rewardCoins = 10 + (stars * 5)
            levelDao.updateCoins(currentCoins + rewardCoins)
        }
    }

    fun useHint() {
        val currentState = _uiState.value
        val coins = playerStats.value?.coins ?: 0
        if (currentState.status == GameStatus.PLAYING && coins >= 10) {
            val targetTile = currentState.tiles.find { it.number == currentState.currentTarget }
            if (targetTile != null) {
                viewModelScope.launch {
                    levelDao.updateCoins(coins - 10)
                    _uiState.update { it.copy(highlightedTileId = targetTile.id) }
                }
            }
        }
    }

    fun useUndo() {
        val currentState = _uiState.value
        val coins = playerStats.value?.coins ?: 0
        if (currentState.status == GameStatus.PLAYING && coins >= 5 && tappedHistory.isNotEmpty()) {
            val lastTile = tappedHistory.removeAt(tappedHistory.size - 1)
            val updatedTiles = currentState.tiles.map {
                if (it.id == lastTile.id) it.copy(isTapped = false) else it
            }
            viewModelScope.launch {
                levelDao.updateCoins(coins - 5)
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

    fun buyCoins(amount: Int) {
        viewModelScope.launch {
            val currentCoins = playerStats.value?.coins ?: 0
            levelDao.updateCoins(currentCoins + amount)
        }
    }

    fun restartGame() {
        val state = _uiState.value
        state.currentDifficulty?.let { startGame(it) } ?: state.currentLevelEntity?.let { startGame(it) }
    }

    fun resetToIdle() {
        _uiState.update { GameUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager?.release()
    }
}

data class GameUiState(
    val tiles: List<Tile> = emptyList(),
    val gridSize: Int = 4,
    val currentTarget: Int = 1,
    val timerSeconds: Int = 0,
    val status: GameStatus = GameStatus.IDLE,
    val score: Int = 0,
    val currentLevelEntity: LevelEntity? = null,
    val currentDifficulty: Difficulty? = null,
    val highlightedTileId: Int? = null
) {
    val timerFormatted: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", timerSeconds / 60, timerSeconds % 60)
}
