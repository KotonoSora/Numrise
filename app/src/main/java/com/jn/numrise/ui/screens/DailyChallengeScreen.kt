package com.jn.numrise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.navigation.Screen
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.NumriseTheme

@Composable
fun DailyChallengeScreen(
    coins: Int,
    onPlay: () -> Unit,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "CHALLENGE",
                coins = coins,
                onBack = onBack,
                onShop = { onNavigate(Screen.CoinShop.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, NeonGreen, RoundedCornerShape(24.dp))
                    .background(NeonGreen.copy(alpha = 0.05f))
                    .padding(24.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription =
                            "Today's Rule: Tap numbers from 1 to 25 in a 4 by 4 grid with a 40 second time limit. Reward is 50 coins."
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    NeonTitle(text = "TODAY'S RULE", fontSize = 24, color = NeonGreen)
                    Spacer(modifier = Modifier.height(32.dp))
                    NeonText(
                        text = "TAP NUMBERS\nFROM 1 TO 25\nIN A 4x4 GRID\n\nTIME LIMIT:\n40 SECONDS",
                        fontSize = 20,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        autoResize = true,
                        maxLines = 6,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    NeonText(text = "REWARD", fontSize = 14, color = NeonYellow)
                    Spacer(modifier = Modifier.height(12.dp))
                    NeonText(
                        text = "50 COINS",
                        fontSize = 32,
                        color = NeonYellow,
                        autoResize = true,
                        maxLines = 1,
                        hasShadow = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            NeonButton(
                text = "START CHALLENGE",
                onClick = onPlay,
                color = NeonGreen,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyChallengeScreenPreview() {
    NumriseTheme {
        DailyChallengeScreen(
            coins = 1200,
            onPlay = {},
            onBack = {},
            onNavigate = {}
        )
    }
}
