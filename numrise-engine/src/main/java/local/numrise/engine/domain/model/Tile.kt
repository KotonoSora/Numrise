package local.numrise.engine.domain.model

data class Tile(
    val id: Int,
    val number: Int,
    val colorIndex: Int,
    val isTapped: Boolean = false
)
