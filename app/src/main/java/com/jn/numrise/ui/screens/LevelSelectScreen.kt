package com.jn.numrise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jn.numrise.data.LevelEntity
import com.jn.numrise.ui.components.NeonIconButton
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.components.NeonTitle
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    levels: List<LevelEntity>,
    onLevelSelected: (LevelEntity) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NeonTitle(
                            text = "SELECT LEVEL",
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(levels) { level ->
                LevelCard(level = level, onClick = { if (level.isUnlocked) onLevelSelected(level) })
            }
        }
    }
}

@Composable
fun LevelCard(level: LevelEntity, onClick: () -> Unit) {
    val borderColor = if (level.isUnlocked) NeonCyan else Color.DarkGray
    val backgroundColor = if (level.isUnlocked) NeonCyan.copy(alpha = 0.1f) else Color.Black

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = level.isUnlocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (level.isUnlocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonText(
                    text = level.id.toString(),
                    fontSize = 18,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    repeat(3) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = if (index < level.stars) NeonYellow else Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        } else {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
