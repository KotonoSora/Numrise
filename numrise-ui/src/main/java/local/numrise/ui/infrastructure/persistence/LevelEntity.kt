package local.numrise.ui.infrastructure.persistence

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
