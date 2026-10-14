package local.numrise.ui.application

import kotlinx.coroutines.flow.first
import local.core.ui.data.CoinAccount
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.model.RunRecord
import local.numrise.engine.domain.repository.LevelRepository
import local.numrise.engine.domain.repository.RunHistoryRepository

/**
 * Records a finished game: level stats + unlock + coin reward + run-history row.
 * Coins flow through core [CoinAccount]; history through core-backed [RunHistoryRepository].
 *
 * Database-only, no legacy migration: levels live in `numrise_levels.db` v1,
 * runs in `game_history.db` v1. Both schemas stay at version 1.
 *
 * @param isWin gates level progress. Failed runs record history (+ zero-reward
 * coins) but must not touch stars/best-time/unlock — callers pass false.
 */
class RecordGameFinishedUseCase(
    private val coinAccount: CoinAccount,
    private val runs: RunHistoryRepository,
    private val levels: LevelRepository
) {
    suspend operator fun invoke(
        record: RunRecord,
        level: Level?,
        score: Int,
        timeElapsed: Int,
        stars: Int,
        isWin: Boolean = true
    ): Boolean {
        val previousHigh = runs.highScoreFlow.first()

        if (isWin && level != null) {
            val updatedLevel = level.copy(
                highScore = maxOf(level.highScore, score),
                bestTimeSeconds = minOf(level.bestTimeSeconds, timeElapsed),
                stars = maxOf(level.stars, stars)
            )
            levels.updateLevel(updatedLevel)

            levels.getLevelById(level.id + 1)?.let { nextLevel ->
                if (!nextLevel.isUnlocked) {
                    levels.updateLevel(nextLevel.copy(isUnlocked = true))
                }
            }
        }

        if (record.reward > 0) {
            coinAccount.addCoins(record.reward)
        }
        runs.recordRun(record)

        return record.score > 0 && record.score > previousHigh
    }
}
