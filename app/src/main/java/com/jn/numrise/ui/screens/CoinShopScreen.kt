package com.jn.numrise.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.numrise.R
import com.jn.numrise.ui.components.*
import com.jn.numrise.ui.theme.*

data class CoinPack(
    val id: String,
    val amount: Int,
    val price: String,
    val color: Color,
    val iconRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinShopScreen(
    coins: Int,
    onBuyPack: (CoinPack) -> Unit,
    onBack: () -> Unit
) {
    val packs = listOf(
        CoinPack("coins_100", 100, "$0.99", NeonPurple, R.drawable.ic_chest_small),
        CoinPack("coins_500", 500, "$4.99", NeonYellow, R.drawable.ic_chest_medium),
        CoinPack("coins_1000", 1000, "$9.99", NeonCyan, R.drawable.ic_chest_large),
        CoinPack("coins_4000", 4000, "$39.99", NeonPink, R.drawable.ic_chest_huge)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NeonTitle(
                            text = "COIN SHOP",
                            fontSize = 20,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NeonText(
                                text = "COINS: $coins",
                                fontSize = 10,
                                color = NeonYellow
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🪙", fontSize = 16.sp)
                        }
                    }
                },
                navigationIcon = {
                    NeonIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        tint = NeonYellow
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            items(packs) { pack ->
                RetroCoinPackItem(pack = pack, onBuy = { onBuyPack(pack) })
            }
        }
    }
}

@Composable
fun RetroCoinPackItem(pack: CoinPack, onBuy: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, pack.color, RoundedCornerShape(16.dp))
            .background(pack.color.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = pack.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                NeonText(
                    text = "${pack.amount} COINS",
                    fontSize = 14,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                NeonText(
                    text = "PRICE: ${pack.price}",
                    fontSize = 10,
                    color = pack.color
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeonButton(
                    text = "PURCHASE",
                    onClick = onBuy,
                    color = pack.color,
                    height = 40,
                    fontSize = 12,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
