package com.jn.numrise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HOW TO PLAY") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
