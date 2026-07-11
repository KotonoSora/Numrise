package com.jn.numrise.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.billing.BillingConstants
import com.jn.numrise.domain.model.CoinPack
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.theme.NumriseTheme

@Composable
fun CoinShopScreen(
    coins: Int,
    packs: List<CoinPack>,
    onBuyPack: (CoinPack) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "COIN SHOP",
                coins = coins,
                onBack = onBack,
                onShop = {} // Already in shop
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(packs) { pack ->
                CompactCoinPackItem(pack = pack, onBuy = { onBuyPack(pack) })
            }
        }
    }
}

@Composable
fun CompactCoinPackItem(pack: CoinPack, onBuy: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, pack.color, RoundedCornerShape(12.dp))
            .background(pack.color.copy(alpha = 0.05f))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = pack.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            NeonText(
                text = "${pack.amount}",
                fontSize = 14,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            NeonText(
                text = "COINS",
                fontSize = 8,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            NeonText(
                text = pack.price,
                fontSize = 10,
                color = pack.color,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            NeonButton(
                text = "BUY",
                onClick = onBuy,
                color = pack.color,
                height = 36,
                fontSize = 10,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoinShopScreenPreview() {
    val mockPacks = BillingConstants.PRODUCT_IDS.map { id ->
        CoinPack(
            id = id,
            amount = BillingConstants.getAmount(id),
            price = BillingConstants.getPrice(id),
            color = BillingConstants.getColor(id),
            iconRes = BillingConstants.getIcon(id),
            productId = id
        )
    }
    NumriseTheme {
        CoinShopScreen(
            coins = 250,
            packs = mockPacks,
            onBuyPack = {},
            onBack = {}
        )
    }
}
