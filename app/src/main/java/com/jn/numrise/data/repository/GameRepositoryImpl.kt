package com.jn.numrise.data.repository

import com.jn.numrise.data.LevelDao
import com.jn.numrise.data.LevelEntity
import com.jn.numrise.data.PlayerStatsEntity
import com.jn.numrise.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(private val levelDao: LevelDao) : GameRepository {
    override fun getAllLevels(): Flow<List<LevelEntity>> = levelDao.getAllLevels()

    override suspend fun getLevelById(levelId: Int): LevelEntity? = levelDao.getLevelById(levelId)

    override suspend fun updateLevel(level: LevelEntity) = levelDao.updateLevel(level)

    override fun getPlayerStats(): Flow<PlayerStatsEntity?> = levelDao.getPlayerStats()

    override suspend fun updateCoins(newCoins: Int) = levelDao.updateCoins(newCoins)
}
