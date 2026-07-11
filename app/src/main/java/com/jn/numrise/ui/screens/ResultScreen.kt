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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.NumriseTheme

@Composable
fun ResultScreen(
    coins: Int,
    score: Int,
    time: String,
    reward: Int,
    isWin: Boolean = true,
    onNextLevel: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    onShop: () -> Unit
) {
    val titleColor = if (isWin) NeonGreen else NeonPink
    val titleText = if (isWin) "LEVEL COMPLETE!" else "GAME OVER"

    Scaffold(
        topBar = {
            NeonHeaderBar(
                coins = coins,
                onShop = onShop
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
            NeonTitle(
                text = titleText,
                fontSize = 28,
                color = titleColor
            )

            if (reward > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                NeonText(text = "+$reward COINS", fontSize = 16, color = NeonYellow)
            }

            Spacer(modifier = Modifier.height(32.dp))

            ResultStatCard("SCORE", score.toString(), NeonCyan)
            if (isWin) {
                Spacer(modifier = Modifier.height(16.dp))
                ResultStatCard("TIME", time, NeonYellow)
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (isWin) {
                NeonButton(
                    text = "NEXT LEVEL",
                    onClick = onNextLevel,
                    color = NeonGreen,
                    icon = Icons.Default.SkipNext,
                    modifier = Modifier.fillMaxWidth(),
                    height = 56
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            NeonButton(
                text = "RETRY",
                onClick = onRestart,
                color = NeonCyan,
                icon = Icons.Default.Refresh,
                modifier = Modifier.fillMaxWidth(),
                height = 56
            )

            Spacer(modifier = Modifier.height(16.dp))

            NeonButton(
                text = "HOME",
                onClick = onHome,
                color = NeonPink,
                icon = Icons.Default.Home,
                modifier = Modifier.fillMaxWidth(),
                height = 56
            )
        }
    }
}

@Composable
fun ResultStatCard(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.05f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonText(text = label, fontSize = 10, color = color.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            NeonText(
                text = value,
                fontSize = 24,
                color = Color.White,
                autoResize = true,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenWinPreview() {
    NumriseTheme {
        ResultScreen(
            coins = 500,
            score = 15400,
            time = "00:45",
            reward = 50,
            isWin = true,
            onNextLevel = {},
            onRestart = {},
            onHome = {},
            onShop = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenFailPreview() {
    NumriseTheme {
        ResultScreen(
            coins = 500,
            score = 2500,
            time = "01:00",
            reward = 0,
            isWin = false,
            onNextLevel = {},
            onRestart = {},
            onHome = {},
            onShop = {}
        )
    }
}
