package com.jn.numrise.viewmodel

import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.Tile
import java.util.Locale

data class GameUiState(
    val tiles: List<Tile> = emptyList(),
    val gridSize: Int = 4,
    val currentTarget: Int = 1,
    val timerSeconds: Int = 0,
    val status: GameStatus = GameStatus.IDLE,
    val score: Int = 0,
    val currentLevel: Level? = null,
    val currentDifficulty: Difficulty? = null,
    val highlightedTileId: Int? = null,
    val coins: Int = 0,
    val levels: List<Level> = emptyList(),
    val soundEnabled: Boolean = true
) {
    val timerFormatted: String
        get() = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            timerSeconds / 60,
            timerSeconds % 60
        )
}

sealed class GameIntent {
    data class StartWithDifficulty(val difficulty: Difficulty) : GameIntent()
    data class StartWithLevel(val level: Level) : GameIntent()
    data class TileTapped(val tile: Tile) : GameIntent()
    object PauseGame : GameIntent()
    object ResumeGame : GameIntent()
    object RestartGame : GameIntent()
    object UseHint : GameIntent()
    object UseUndo : GameIntent()
    object ResetToIdle : GameIntent()
    data class BuyCoins(val amount: Int) : GameIntent()
    data class SetSoundEnabled(val enabled: Boolean) : GameIntent()
}
