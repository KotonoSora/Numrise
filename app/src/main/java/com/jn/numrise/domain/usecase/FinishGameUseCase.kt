package com.jn.numrise.domain.usecase

import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.Tile

class FinishGameUseCase {
    fun execute(
        currentScore: Int,
        timerSeconds: Int,
        currentDifficulty: Difficulty?,
        currentLevel: Level?,
        finalTiles: List<Tile>
    ): GameResult {
        val timeLimit = currentDifficulty?.timeLimit ?: 60
        val timeElapsed = timeLimit - timerSeconds
        val timeBonus = timerSeconds * 10
        val finalScore = currentScore + timeBonus

        val stars = when {
            timerSeconds > timeLimit * 0.6 -> 3
            timerSeconds > timeLimit * 0.3 -> 2
            else -> 1
        }

        val reward = if (currentLevel != null) 20 + (stars * 10) else 10 + (stars * 5)

        return GameResult(
            score = finalScore,
            stars = stars,
            reward = reward,
            timeElapsed = timeElapsed,
            finalTiles = finalTiles
        )
    }
}

data class GameResult(
    val score: Int,
    val stars: Int,
    val reward: Int,
    val timeElapsed: Int,
    val finalTiles: List<Tile>
)
