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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.numrise.ui.presentation.components.NeonButton
import local.numrise.ui.presentation.components.NeonHeaderBar
import local.numrise.ui.presentation.components.NeonTitle
import local.numrise.ui.presentation.navigation.Screen
import local.numrise.ui.presentation.theme.NeonCyan
import local.numrise.ui.presentation.theme.NeonGreen
import local.numrise.ui.presentation.theme.NeonPink
import local.numrise.ui.presentation.theme.NeonPurple
import local.numrise.ui.presentation.theme.NeonYellow
import local.numrise.ui.presentation.theme.NumriseTheme

@Composable
fun HomeScreen(
    coins: Int = 0,
    onNavigate: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                coins = coins,
                onShop = { onNavigate(Screen.CoinShop.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonTitle(
                text = "NUMRISE",
                fontSize = 48,
                color = NeonPink,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            NeonMenuButton("PLAY GAME", NeonCyan) { onNavigate(Screen.DifficultySelect.route) }
            NeonMenuButton("DAILY CHALLENGE", NeonGreen) { onNavigate(Screen.DailyChallenge.route) }
            NeonMenuButton("LEADERBOARD", NeonPurple) { onNavigate(Screen.Leaderboard.route) }
            NeonMenuButton("HELP", NeonYellow) { onNavigate(Screen.Help.route) }
            NeonMenuButton("SETTING", NeonPink) { onNavigate(Screen.Settings.route) }
        }
    }
}

@Composable
fun NeonMenuButton(text: String, color: Color, onClick: () -> Unit) {
    NeonButton(
        text = text,
        onClick = onClick,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        height = 56,
        fontSize = 14
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NumriseTheme {
        HomeScreen(
            coins = 1250,
            onNavigate = {}
        )
    }
}
