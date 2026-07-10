package com.jn.numrise.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jn.numrise.domain.model.Difficulty
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonIconButton
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectScreen(
    onDifficultySelected: (Difficulty) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NeonTitle(
                            text = "SELECT DIFFICULTY",
                            fontSize = 20,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    NeonIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        tint = NeonCyan
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
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
        fontSize = 14
    )
}
