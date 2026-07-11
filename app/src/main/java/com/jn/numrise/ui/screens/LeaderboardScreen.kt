package com.jn.numrise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.domain.model.History
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.navigation.Screen
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.NumriseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    coins: Int,
    history: List<History>,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "HISTORY",
                coins = coins,
                onBack = onBack,
                onShop = { onNavigate(Screen.CoinShop.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                NeonText(text = "NO HISTORY YET", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeonText(
                            text = "DATE",
                            fontSize = 10,
                            color = Color.Gray,
                            modifier = Modifier.weight(1.2f)
                        )
                        NeonText(
                            text = "SCORE",
                            fontSize = 10,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        NeonText(
                            text = "REWARD",
                            fontSize = 10,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
                items(history) { entry ->
                    HistoryItem(entry)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(entry: History) {
    val date = Date(entry.date)
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .background(NeonGreen.copy(alpha = 0.02f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Date & Time
            Column(modifier = Modifier.weight(1.2f)) {
                NeonText(
                    text = dateStr,
                    fontSize = 11,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                NeonText(
                    text = timeStr,
                    fontSize = 10,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            // Column 2: Score
            NeonText(
                text = "${entry.score}",
                fontSize = 13,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Column 3: Reward
            NeonText(
                text = if (entry.reward > 0) "+${entry.reward}" else "0",
                fontSize = 13,
                color = if (entry.reward > 0) NeonYellow else Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    val mockHistory = listOf(
        History(date = System.currentTimeMillis(), score = 15000, reward = 50, isWin = true),
        History(
            date = System.currentTimeMillis() - 86400000,
            score = 8000,
            reward = 20,
            isWin = true
        ),
        History(
            date = System.currentTimeMillis() - 172800000,
            score = 1200,
            reward = 0,
            isWin = false
        )
    )
    NumriseTheme {
        LeaderboardScreen(
            coins = 1200,
            history = mockHistory,
            onBack = {},
            onNavigate = {}
        )
    }
}
