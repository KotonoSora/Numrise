package com.jn.numrise.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.navigation.Screen
import com.jn.numrise.ui.theme.NumriseTheme

@Composable
fun DifficultySelectScreen(
    coins: Int,
    onDifficultySelected: (Difficulty) -> Unit,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "DIFFICULTY",
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
            Difficulty.entries.forEach { difficulty ->
                DifficultyButton(difficulty) { onDifficultySelected(difficulty) }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DifficultyButton(difficulty: Difficulty, onClick: () -> Unit) {
    NeonButton(
        text = "${difficulty.label}\n(${difficulty.maxNumber} NUMS - ${difficulty.timeLimit}s)",
        onClick = onClick,
        color = difficulty.color,
        modifier = Modifier.fillMaxWidth(),
        height = 80,
        fontSize = 14,
        maxLines = 2
    )
}

@Preview(showBackground = true)
@Composable
fun DifficultySelectScreenPreview() {
    NumriseTheme {
        DifficultySelectScreen(
            coins = 1200,
            onDifficultySelected = {},
            onBack = {},
            onNavigate = {}
        )
    }
}
