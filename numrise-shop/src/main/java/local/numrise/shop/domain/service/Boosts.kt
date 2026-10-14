package local.numrise.shop.domain.service

import local.numrise.shop.domain.model.BoostIds

object Boosts {
    const val HINT_COST = 50
    const val UNDO_COST = 50
    const val EXTRA_TIME_COST = 30

    const val EXTRA_TIME_SECONDS = 60

    fun cheapestCost(): Int = minOf(HINT_COST, UNDO_COST, EXTRA_TIME_COST)

    fun costFor(boostId: String): Int? = when (boostId) {
        BoostIds.BOOST_HINT_ID -> HINT_COST
        BoostIds.BOOST_UNDO_ID -> UNDO_COST
        BoostIds.BOOST_EXTRA_TIME_ID -> EXTRA_TIME_COST
        else -> null
    }
}
