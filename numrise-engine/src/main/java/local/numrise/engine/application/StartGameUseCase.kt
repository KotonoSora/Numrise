package local.numrise.engine.application

import local.numrise.engine.domain.model.Difficulty
import local.numrise.engine.domain.model.GameStatus
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.model.Tile
import java.time.LocalDate

data class GameInitialState(
    val tiles: List<Tile>,
    val gridSize: Int,
    val currentTarget: Int,
    val timerSeconds: Int,
    val status: GameStatus,
    val currentDifficulty: Difficulty? = null,
    val currentLevel: Level? = null
)

class StartGameUseCase {
    fun execute(difficulty: Difficulty): GameInitialState {
        val gridSize = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 4
            Difficulty.HARD -> 5
            Difficulty.VERY_HARD -> 6
        }

        return GameInitialState(
            tiles = dealTiles(difficulty.maxNumber),
            gridSize = gridSize,
            currentTarget = 1,
            timerSeconds = difficulty.timeLimit,
            status = GameStatus.PLAYING,
            currentDifficulty = difficulty
        )
    }

    fun execute(level: Level): GameInitialState {
        val totalTiles = level.gridSize * level.gridSize

        return GameInitialState(
            tiles = dealTiles(totalTiles),
            gridSize = level.gridSize,
            currentTarget = 1,
            timerSeconds = 60,
            status = GameStatus.PLAYING,
            currentLevel = level
        )
    }

    /**
     * Deterministic daily board: seeded by [epochDay] (defaults to today), so
     * every launch on the same date deals the same 4x4 board.
     */
    fun executeDailyChallenge(epochDay: Long = LocalDate.now().toEpochDay()): GameInitialState {
        return GameInitialState(
            tiles = dealTiles(16, kotlin.random.Random(epochDay)),
            gridSize = 4,
            currentTarget = 1,
            timerSeconds = 40,
            status = GameStatus.PLAYING
        )
    }

    private fun dealTiles(
        totalTiles: Int,
        random: kotlin.random.Random = kotlin.random.Random.Default
    ): List<Tile> =
        (1..totalTiles).shuffled(random).mapIndexed { index, number ->
            Tile(
                id = index,
                number = number,
                colorIndex = index % TILE_COLOR_COUNT
            )
        }

    companion object {
        const val TILE_COLOR_COUNT = 6
    }
}
