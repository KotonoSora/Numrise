package local.numrise.engine.application

import local.numrise.engine.domain.model.Tile

sealed class TapResult {
    data class Correct(val updatedTiles: List<Tile>, val nextTarget: Int, val newScore: Int) :
        TapResult()

    data class Incorrect(val newScore: Int) : TapResult()
    data class Finished(val finalTiles: List<Tile>) : TapResult()
}

class ProcessTileTapUseCase {
    fun execute(
        tile: Tile,
        currentTarget: Int,
        currentTiles: List<Tile>,
        currentScore: Int
    ): TapResult {
        if (tile.number == currentTarget) {
            val updatedTiles = currentTiles.map {
                if (it.id == tile.id) it.copy(isTapped = true) else it
            }

            val nextTarget = currentTarget + 1
            val isFinished = nextTarget > currentTiles.size

            return if (isFinished) {
                TapResult.Finished(updatedTiles)
            } else {
                TapResult.Correct(
                    updatedTiles = updatedTiles,
                    nextTarget = nextTarget,
                    newScore = currentScore + 100
                )
            }
        } else {
            return TapResult.Incorrect(
                newScore = maxOf(0, currentScore - 50)
            )
        }
    }
}
