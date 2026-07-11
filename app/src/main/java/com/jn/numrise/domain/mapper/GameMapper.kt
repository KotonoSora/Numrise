package com.jn.numrise.domain.mapper

import com.jn.numrise.data.HistoryEntity
import com.jn.numrise.data.LevelEntity
import com.jn.numrise.data.PlayerStatsEntity
import com.jn.numrise.domain.model.History
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.PlayerStats

object GameMapper {
    fun mapToLevel(entity: LevelEntity): Level {
        return Level(
            id = entity.id,
            gridSize = entity.gridSize,
            highScore = entity.highScore,
            bestTimeSeconds = entity.bestTimeSeconds,
            isUnlocked = entity.isUnlocked,
            stars = entity.stars
        )
    }

    fun mapToLevelEntity(domain: Level): LevelEntity {
        return LevelEntity(
            id = domain.id,
            gridSize = domain.gridSize,
            highScore = domain.highScore,
            bestTimeSeconds = domain.bestTimeSeconds,
            isUnlocked = domain.isUnlocked,
            stars = domain.stars
        )
    }

    fun mapToPlayerStats(entity: PlayerStatsEntity?): PlayerStats {
        return PlayerStats(
            coins = entity?.coins ?: 0,
            soundEnabled = entity?.soundEnabled ?: true,
            levelsCompleted = 0 // Placeholder if not in entity
        )
    }

    fun mapToHistory(entity: HistoryEntity): History {
        return History(
            id = entity.id,
            date = entity.date,
            score = entity.score,
            reward = entity.reward,
            levelId = entity.levelId,
            difficulty = entity.difficulty,
            isWin = entity.isWin
        )
    }

    fun mapToHistoryEntity(domain: History): HistoryEntity {
        return HistoryEntity(
            id = domain.id,
            date = domain.date,
            score = domain.score,
            reward = domain.reward,
            levelId = domain.levelId,
            difficulty = domain.difficulty,
            isWin = domain.isWin
        )
    }
}
