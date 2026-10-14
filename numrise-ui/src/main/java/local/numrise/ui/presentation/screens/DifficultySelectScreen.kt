package local.numrise.ui.presentation.screens

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
import local.numrise.engine.domain.model.Difficulty
import local.numrise.ui.presentation.components.NeonButton
import local.numrise.ui.presentation.components.NeonHeaderBar
import local.numrise.ui.presentation.navigation.Screen
import local.numrise.ui.presentation.theme.NeonCyan
import local.numrise.ui.presentation.theme.NeonGreen
import local.numrise.ui.presentation.theme.NeonPink
import local.numrise.ui.presentation.theme.NeonYellow
import local.numrise.ui.presentation.theme.NumriseTheme

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
    val color = when (difficulty) {
        Difficulty.EASY -> NeonGreen
        Difficulty.MEDIUM -> NeonCyan
        Difficulty.HARD -> NeonYellow
        Difficulty.VERY_HARD -> NeonPink
    }
    NeonButton(
        text = "${difficulty.label}\n(${difficulty.maxNumber} NUMS - ${difficulty.timeLimit}s)",
        onClick = onClick,
        color = color,
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
