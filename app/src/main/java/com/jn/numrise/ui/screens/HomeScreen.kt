package com.jn.numrise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.navigation.Screen
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonPurple
import com.jn.numrise.ui.theme.NeonYellow

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
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
            text = "NUMRISE",
            fontSize = 48,
            color = NeonPink
        )
        
        Spacer(modifier = Modifier.height(64.dp))

        NeonMenuButton("START GAME", NeonCyan) { onNavigate(Screen.DifficultySelect.route) }
        NeonMenuButton("COIN SHOP", NeonYellow) { onNavigate(Screen.CoinShop.route) }
        NeonMenuButton("SETTINGS", NeonPurple) { onNavigate(Screen.Settings.route) }
        NeonMenuButton("HELP", Color.White) { onNavigate(Screen.Help.route) }
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
            .padding(vertical = 10.dp),
        height = 60,
        fontSize = 14
    )
}
