package com.jn.numrise.domain.usecase

import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.Tile
import com.jn.numrise.domain.model.getTileColors

class StartGameUseCase {
    fun execute(difficulty: Difficulty): GameInitialState {
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

        return GameInitialState(
            tiles = tiles,
            gridSize = gridSize,
            currentTarget = 1,
            timerSeconds = difficulty.timeLimit,
            status = GameStatus.PLAYING,
            currentDifficulty = difficulty
        )
    }

    fun execute(level: Level): GameInitialState {
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

        return GameInitialState(
            tiles = tiles,
            gridSize = level.gridSize,
            currentTarget = 1,
            timerSeconds = 60, // Default for level entity as in original code
            status = GameStatus.PLAYING,
            currentLevel = level
        )
    }
}

data class GameInitialState(
    val tiles: List<Tile>,
    val gridSize: Int,
    val currentTarget: Int,
    val timerSeconds: Int,
    val status: GameStatus,
    val currentDifficulty: Difficulty? = null,
    val currentLevel: Level? = null
)
