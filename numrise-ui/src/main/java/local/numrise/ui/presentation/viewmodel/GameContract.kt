package local.numrise.ui.presentation.viewmodel

import java.util.Locale
import local.numrise.engine.domain.model.Difficulty
import local.numrise.engine.domain.model.GameStatus
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.model.RunRecord
import local.numrise.engine.domain.model.Tile

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
    val soundEnabled: Boolean = true,
    val history: List<RunRecord> = emptyList(),
    val lastReward: Int = 0
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
    object RestartGame : GameIntent()
    object UseHint : GameIntent()
    object UseUndo : GameIntent()
    object UseExtraTime : GameIntent()
    object ResetToIdle : GameIntent()
    object StartDailyChallenge : GameIntent()
    object StartNext : GameIntent()
}
