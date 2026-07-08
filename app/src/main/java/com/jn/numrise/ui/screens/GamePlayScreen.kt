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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.numrise.model.GameStatus
import com.jn.numrise.model.Tile
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonIconButton
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPink
import com.jn.numrise.ui.theme.NeonPurple
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.PressStart2P
import com.jn.numrise.viewmodel.GameViewModel

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onPause: () -> Unit,
    onFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats by viewModel.playerStats.collectAsState()

    // Navigation triggers
    LaunchedEffect(uiState.status) {
        if (uiState.status == GameStatus.FINISHED) {
            onFinished()
        }
    }

    Scaffold(
        topBar = {
            GameTopBar(
                timerText = uiState.timerFormatted,
                score = uiState.score,
                coins = stats?.coins ?: 0,
                onPauseClick = {
                    viewModel.pauseGame()
                    onPause()
                }
            )
        },
        bottomBar = {
            GameBottomBar(
                onHint = { viewModel.useHint() },
                onUndo = { viewModel.useUndo() }
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiState.gridSize),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.tiles, key = { it.id }) { tile ->
                    NumberTile(
                        tile = tile,
                        isHighlighted = tile.id == uiState.highlightedTileId,
                        onClick = { viewModel.onTileTapped(tile) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameTopBar(
    timerText: String,
    score: Int,
    coins: Int,
    onPauseClick: () -> Unit
) {
    Column(modifier = Modifier
        .statusBarsPadding()
        .background(Color.Black)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = NeonYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                NeonText(text = coins.toString(), fontSize = 12, color = NeonYellow)
            }

            NeonIconButton(
                icon = Icons.Default.Pause,
                onClick = onPauseClick,
                tint = NeonPink
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                NeonText(
                    text = timerText,
                    fontSize = 18,
                    color = NeonCyan
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                NeonText(
                    text = "SCORE: $score",
                    fontSize = 12,
                    color = NeonGreen
                )
            }
        }

        // Neon divider
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NeonCyan.copy(alpha = 0.3f)))
    }
}

@Composable
fun NumberTile(
    tile: Tile,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (tile.isTapped) Color.DarkGray.copy(alpha = 0.3f) else tile.color.copy(alpha = 0.8f)
    val borderColor = if (isHighlighted) Color.White else tile.color
    val glowColor = if (isHighlighted) Color.White else tile.color

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isHighlighted) 3.dp else 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !tile.isTapped) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!tile.isTapped) {
            Text(
                text = tile.number.toString(),
                style = TextStyle(
                    fontFamily = PressStart2P,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = glowColor,
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

@Composable
fun GameBottomBar(
    onHint: () -> Unit,
    onUndo: () -> Unit
) {
    Column(modifier = Modifier.background(Color.Black)) {
        // Neon divider
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NeonCyan.copy(alpha = 0.3f)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NeonAssistButton(
                icon = Icons.Default.Lightbulb,
                label = "HINT (10)",
                color = NeonYellow,
                onClick = onHint
            )
            NeonAssistButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "UNDO (5)",
                color = NeonPurple,
                onClick = onUndo
            )
        }
    }
}

@Composable
fun NeonAssistButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    NeonButton(
        text = label,
        onClick = onClick,
        color = color,
        icon = icon,
        height = 48,
        fontSize = 10,
        modifier = Modifier.width(160.dp)
    )
}
