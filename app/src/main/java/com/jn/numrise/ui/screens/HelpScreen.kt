package com.jn.numrise.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.navigation.Screen
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NumriseTheme

@Composable
fun HelpScreen(
    coins: Int,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "HOW TO PLAY",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HelpSection(
                title = "Objective",
                description = "Tap the numbers in the grid in ascending order starting from 1. Clear all numbers to finish the level!"
            )

            HelpSection(
                title = "Hints & Undos",
                description = "Stuck? Use a Hint (10 coins) to see the next number. Made a mistake? Use Undo (5 coins) to revert your last correct move."
            )

            HelpSection(
                title = "Earning Coins",
                description = "Complete levels quickly to earn more coins. You can also buy coin packs in the Shop."
            )

            HelpSection(
                title = "Progression",
                description = "Unlock new levels by completing the previous ones. Higher levels have larger grids and more numbers!"
            )
        }
    }
}

@Composable
fun HelpSection(title: String, description: String) {
    Column {
        NeonText(
            text = title,
            fontSize = 18,
            color = NeonCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    NumriseTheme {
        HelpScreen(
            coins = 100,
            onBack = {},
            onNavigate = {}
        )
    }
}
