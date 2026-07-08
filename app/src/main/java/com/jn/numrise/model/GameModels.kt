package com.jn.numrise.model

import androidx.compose.ui.graphics.Color
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.TileBlue
import com.jn.numrise.ui.theme.TileCyan
import com.jn.numrise.ui.theme.TileGreen
import com.jn.numrise.ui.theme.TileOrange
import com.jn.numrise.ui.theme.TilePink
import com.jn.numrise.ui.theme.TilePurple

data class Tile(
    val id: Int,
    val number: Int,
    val color: Color,
    var isTapped: Boolean = false
)

enum class GameStatus {
    IDLE, PLAYING, PAUSED, FINISHED, FAILED
}

enum class Difficulty(val label: String, val maxNumber: Int, val timeLimit: Int, val color: Color) {
    EASY("EASY", 9, 30, NeonGreen),
    MEDIUM("MEDIUM", 20, 45, NeonCyan),
    HARD("HARD", 50, 55, NeonYellow),
    VERY_HARD("VERY HARD", 60, 60, NeonPink)
}

data class GameLevel(
    val id: Int,
    val gridSize: Int, // 3 for 3x3, 4 for 4x4
    val targetTime: Int, // in seconds
    val baseScore: Int
)

fun getTileColors(): List<Color> = listOf(
    TilePink, TileGreen, TileBlue, TileOrange, TilePurple, TileCyan
)
