package local.numrise.engine.domain.model

data class Level(
    val id: Int,
    val gridSize: Int,
    val highScore: Int,
    val bestTimeSeconds: Int,
    val isUnlocked: Boolean,
    val stars: Int
)
