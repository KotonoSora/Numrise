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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonYellow

@Composable
fun ResultScreen(
    score: Int,
    time: String,
    onNextLevel: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeonTitle(
            text = "LEVEL COMPLETE!",
            fontSize = 24,
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(48.dp))

        ResultStatCard("SCORE", score.toString(), NeonCyan)
        Spacer(modifier = Modifier.height(16.dp))
        ResultStatCard("TIME", time, NeonYellow)

        Spacer(modifier = Modifier.height(64.dp))

        NeonButton(
            text = "NEXT LEVEL",
            onClick = onNextLevel,
            color = NeonGreen,
            icon = Icons.Default.SkipNext,
            modifier = Modifier.fillMaxWidth(),
            height = 56
        )

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            text = "RESTART",
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
                color = Color.White
            )
        }
    }
}
