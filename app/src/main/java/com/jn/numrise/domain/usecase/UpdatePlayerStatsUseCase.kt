package com.jn.numrise.domain.usecase

import com.jn.numrise.domain.mapper.GameMapper
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.repository.GameRepository
import kotlinx.coroutines.flow.first

class UpdatePlayerStatsUseCase(private val repository: GameRepository) {
    suspend fun updateGameStats(
        level: Level,
        score: Int,
        timeElapsed: Int,
        stars: Int,
        rewardCoins: Int
    ) {
        val updatedLevel = level.copy(
            highScore = maxOf(level.highScore, score),
            bestTimeSeconds = minOf(level.bestTimeSeconds, timeElapsed),
            stars = maxOf(level.stars, stars)
        )
        repository.updateLevel(GameMapper.mapToLevelEntity(updatedLevel))

        // Unlock next level
        repository.getLevelById(level.id + 1)?.let { nextLevelEntity ->
            if (!nextLevelEntity.isUnlocked) {
                repository.updateLevel(nextLevelEntity.copy(isUnlocked = true))
            }
        }

        // Award coins
        val stats = repository.getPlayerStats().first()
        val currentCoins = stats?.coins ?: 0
        repository.updateCoins(currentCoins + rewardCoins)
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        repository.updateSoundEnabled(enabled)
    }

    suspend fun addCoins(amount: Int) {
        val stats = repository.getPlayerStats().first()
        val currentCoins = stats?.coins ?: 0
        repository.updateCoins(currentCoins + amount)
    }

    suspend fun spendCoins(amount: Int): Boolean {
        val stats = repository.getPlayerStats().first()
        val currentCoins = stats?.coins ?: 0
        return if (currentCoins >= amount) {
            repository.updateCoins(currentCoins - amount)
            true
        } else {
            false
        }
    }
}
