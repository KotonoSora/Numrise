package local.numrise.ui.presentation.screens

import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.core.billing.BillingStatus
import local.core.shop.coins.CoinPackItem
import local.numrise.ui.presentation.components.NeonButton
import local.numrise.ui.presentation.components.NeonHeaderBar
import local.numrise.ui.presentation.components.NeonText
import local.numrise.ui.presentation.theme.NeonCyan
import local.numrise.ui.presentation.theme.NeonYellow
import local.numrise.ui.presentation.theme.NumriseTheme
import local.numrise.ui.presentation.viewmodel.ShopViewModel

@Composable
fun CoinShopScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit
) {
    val packs by viewModel.products.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val status by viewModel.status.collectAsState()
    val activity = LocalActivity.current

    CoinShopContent(
        coins = coins,
        packs = packs,
        status = status,
        onBuyPack = { pack ->
            activity?.let { viewModel.buyCoinPack(it, pack) }
        },
        onRetry = { viewModel.retryConnection() },
        onBack = onBack
    )
}

@Composable
fun CoinShopContent(
    coins: Int,
    packs: List<CoinPackItem>,
    status: BillingStatus,
    onBuyPack: (CoinPackItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "COIN SHOP",
                coins = coins,
                onBack = onBack,
                onShop = {}
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            status == BillingStatus.ERROR -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NeonText(
                        text = "Store currently unavailable",
                        color = Color.Gray,
                        fontSize = 14,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonButton(
                        text = "TRY AGAIN",
                        onClick = onRetry,
                        color = NeonCyan,
                        height = 48,
                        fontSize = 12,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            else -> {
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
                        CompactCoinPackItem(
                            pack = pack,
                            onBuy = { onBuyPack(pack) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactCoinPackItem(pack: CoinPackItem, onBuy: () -> Unit) {
    val accent = NeonYellow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, accent, RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.05f))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = pack.imageRes),
                contentDescription = pack.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            NeonText(
                text = "${pack.coinAmount}",
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
                color = accent,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            NeonButton(
                text = "BUY",
                onClick = onBuy,
                color = accent,
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
    NumriseTheme {
        CoinShopContent(
            coins = 250,
            packs = emptyList(),
            status = BillingStatus.CONNECTED,
            onBuyPack = {},
            onRetry = {},
            onBack = {}
        )
    }
}
