package local.numrise.engine.application

import local.numrise.engine.domain.model.Difficulty
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.model.Tile
import local.numrise.engine.domain.service.RewardPolicy

data class GameResult(
    val score: Int,
    val stars: Int,
    val reward: Int,
    val timeElapsed: Int,
    val finalTiles: List<Tile>
)

class FinishGameUseCase {
    fun execute(
        currentScore: Int,
        timerSeconds: Int,
        currentDifficulty: Difficulty?,
        currentLevel: Level?,
        finalTiles: List<Tile>
    ): GameResult {
        val timeLimit = currentDifficulty?.timeLimit ?: 60
        val timeElapsed = RewardPolicy.timeElapsed(timeLimit, timerSeconds)
        val finalScore = RewardPolicy.finalScore(currentScore, timerSeconds)
        val stars = RewardPolicy.starsFor(timerSeconds, timeLimit)
        val reward = RewardPolicy.rewardFor(stars, currentLevel != null)

        return GameResult(
            score = finalScore,
            stars = stars,
            reward = reward,
            timeElapsed = timeElapsed,
            finalTiles = finalTiles
        )
    }
}
