package com.jn.numrise.domain.repository

import com.jn.numrise.domain.model.History
import com.jn.numrise.domain.model.Level
import com.jn.numrise.domain.model.PlayerStats
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllLevels(): Flow<List<Level>>
    suspend fun getLevelById(levelId: Int): Level?
    suspend fun updateLevel(level: Level)
    fun getPlayerStats(): Flow<PlayerStats>
    suspend fun updateCoins(newCoins: Int)
    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun saveHistory(history: History)
    fun getAllHistory(): Flow<List<History>>
}
