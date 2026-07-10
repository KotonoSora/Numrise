package com.jn.numrise.domain.repository

import com.jn.numrise.data.LevelEntity
import com.jn.numrise.data.PlayerStatsEntity
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllLevels(): Flow<List<LevelEntity>>
    suspend fun getLevelById(levelId: Int): LevelEntity?
    suspend fun updateLevel(level: LevelEntity)
    fun getPlayerStats(): Flow<PlayerStatsEntity?>
    suspend fun updateCoins(newCoins: Int)
}
