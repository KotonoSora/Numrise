package com.jn.numrise.data.repository

import com.jn.numrise.data.LevelDao
import com.jn.numrise.domain.mapper.GameMapper
import com.jn.numrise.domain.model.History
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.PlayerStats
import com.jn.numrise.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(private val levelDao: LevelDao) : GameRepository {
    override fun getAllLevels(): Flow<List<Level>> = levelDao.getAllLevels().map { entities ->
        entities.map { GameMapper.mapToLevel(it) }
    }

    override suspend fun getLevelById(levelId: Int): Level? =
        levelDao.getLevelById(levelId)?.let { GameMapper.mapToLevel(it) }

    override suspend fun updateLevel(level: Level) =
        levelDao.updateLevel(GameMapper.mapToLevelEntity(level))

    override fun getPlayerStats(): Flow<PlayerStats> = levelDao.getPlayerStats().map { entity ->
        GameMapper.mapToPlayerStats(entity)
    }

    override suspend fun updateCoins(newCoins: Int) = levelDao.updateCoins(newCoins)

    override suspend fun updateSoundEnabled(enabled: Boolean) = levelDao.updateSoundEnabled(enabled)

    override suspend fun saveHistory(history: History) =
        levelDao.insertHistory(GameMapper.mapToHistoryEntity(history))

    override fun getAllHistory(): Flow<List<History>> = levelDao.getAllHistory().map { entities ->
        entities.map { GameMapper.mapToHistory(it) }
    }
}
