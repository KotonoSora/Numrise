package com.jn.numrise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val score: Int,
    val reward: Int,
    val levelId: Int? = null,
    val difficulty: String? = null,
    val isWin: Boolean
)
