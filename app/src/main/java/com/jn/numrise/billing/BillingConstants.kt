package com.jn.numrise.billing

import androidx.compose.ui.graphics.Color
import com.jn.numrise.R
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonPurple
import com.jn.numrise.ui.theme.NeonYellow

object BillingConstants {
    val PRODUCT_IDS = listOf(
        "coins_100", "coins_500", "coins_1000", "coins_1500",
        "coins_2000", "coins_2500", "coins_3000", "coins_3500", "coins_4000"
    )

    fun getAmount(productId: String): Int = productId.substringAfter("coins_").toIntOrNull() ?: 0

    fun getPrice(productId: String): String = when (productId) {
        "coins_100" -> "$0.99"
        "coins_500" -> "$4.99"
        "coins_1000" -> "$9.99"
        "coins_1500" -> "$14.99"
        "coins_2000" -> "$19.99"
        "coins_2500" -> "$24.99"
        "coins_3000" -> "$29.99"
        "coins_3500" -> "$34.99"
        "coins_4000" -> "$39.99"
        else -> "N/A"
    }

    fun getColor(productId: String): Color = when (getAmount(productId)) {
        in 0..499 -> NeonPurple
        in 500..999 -> NeonYellow
        in 1000..1999 -> NeonCyan
        in 2000..2999 -> NeonGreen
        else -> NeonPink
    }

    fun getIcon(productId: String): Int {
        val amount = getAmount(productId)
        return when {
            amount < 500 -> R.drawable.ic_chest_small
            amount < 1500 -> R.drawable.ic_chest_medium
            amount < 3000 -> R.drawable.ic_chest_large
            else -> R.drawable.ic_chest_huge
        }
    }
}
