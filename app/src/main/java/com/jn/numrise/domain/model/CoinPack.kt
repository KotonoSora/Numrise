package com.jn.numrise.domain.model

import androidx.compose.ui.graphics.Color

data class CoinPack(
    val id: String,
    val amount: Int,
    val price: String,
    val color: Color,
    val iconRes: Int,
    val productId: String
)
