package com.kotonosora.numrise.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels ORDER BY id ASC")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE id = :levelId")
    suspend fun getLevelById(levelId: Int): LevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Update
    suspend fun updateLevel(level: LevelEntity)

    @Query("SELECT * FROM player_stats WHERE id = 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStats(stats: PlayerStatsEntity)

    @Query("UPDATE player_stats SET coins = :newCoins WHERE id = 1")
    suspend fun updateCoins(newCoins: Int)
}
