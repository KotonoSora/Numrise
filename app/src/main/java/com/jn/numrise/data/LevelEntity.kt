package com.jn.numrise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey val id: Int,
    val gridSize: Int,
    val isUnlocked: Boolean,
    val highScore: Int = 0,
    val bestTimeSeconds: Int = Int.MAX_VALUE,
    val stars: Int = 0
)

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey val id: Int = 1, // Singleton row
    val coins: Int = 100
)
