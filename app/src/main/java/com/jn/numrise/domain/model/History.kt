package com.jn.numrise.domain.model

data class History(
    val id: Int = 0,
    val date: Long,
    val score: Int,
    val reward: Int,
    val levelId: Int? = null,
    val difficulty: String? = null,
    val isWin: Boolean
)
