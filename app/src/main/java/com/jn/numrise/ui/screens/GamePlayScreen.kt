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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.domain.model.Tile
import com.jn.numrise.domain.model.getTileColors
import com.jn.numrise.ui.components.NeonButton
import com.jn.numrise.ui.components.NeonHeaderBar
import com.jn.numrise.ui.components.NeonText
import com.jn.numrise.ui.theme.NeonCyan
import com.jn.numrise.ui.theme.NeonGreen
import com.jn.numrise.ui.theme.NeonPurple
import com.jn.numrise.ui.theme.NeonYellow
import com.jn.numrise.ui.theme.NumriseTheme
import com.jn.numrise.viewmodel.GameIntent
import com.jn.numrise.viewmodel.GameViewModel

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onFinished: () -> Unit,
    onShop: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigation triggers
    LaunchedEffect(uiState.status) {
        if (uiState.status == GameStatus.FINISHED || uiState.status == GameStatus.FAILED) {
            onFinished()
        }
    }

    GamePlayContent(
        tiles = uiState.tiles,
        gridSize = uiState.gridSize,
        timerText = uiState.timerFormatted,
        score = uiState.score,
        coins = uiState.coins,
        highlightedTileId = uiState.highlightedTileId,
        onTileClick = { viewModel.onIntent(GameIntent.TileTapped(it)) },
        onShop = onShop,
        onHint = { viewModel.onIntent(GameIntent.UseHint) },
        onUndo = { viewModel.onIntent(GameIntent.UseUndo) }
    )
}

@Composable
fun GamePlayContent(
    tiles: List<Tile>,
    gridSize: Int,
    timerText: String,
    score: Int,
    coins: Int,
    highlightedTileId: Int?,
    onTileClick: (Tile) -> Unit,
    onShop: () -> Unit,
    onHint: () -> Unit,
    onUndo: () -> Unit
) {
    Scaffold(
        topBar = {
            GameTopBar(
                timerText = timerText,
                score = score,
                coins = coins,
                onBuyCoins = onShop,
                onHint = onHint,
                onUndo = onUndo
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val tileColors = getTileColors()
                items(tiles, key = { it.id }) { tile ->
                    val colorIndex = tiles.indexOf(tile) % tileColors.size
                    NumberTile(
                        tile = tile,
                        tileColor = tileColors[colorIndex],
                        isHighlighted = tile.id == highlightedTileId,
                        onClick = { onTileClick(tile) }
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
    onBuyCoins: () -> Unit,
    onHint: () -> Unit,
    onUndo: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NeonHeaderBar(
            coins = coins,
            onShop = onBuyCoins,
            onBack = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
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
                Spacer(modifier = Modifier.width(8.dp))
                NeonText(
                    text = "SCORE: $score",
                    fontSize = 14,
                    color = NeonGreen,
                    autoResize = true,
                    maxLines = 1
                )
            }
        }

        // Power-ups moved here
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeonAssistButton(
                icon = Icons.Default.Lightbulb,
                label = "HINT (50)",
                color = NeonYellow,
                onClick = onHint,
                modifier = Modifier.weight(1f)
            )
            NeonAssistButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "UNDO (50)",
                color = NeonPurple,
                onClick = onUndo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NumberTile(
    tile: Tile,
    tileColor: Color,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    // Determine the base color for the tile. tile.color was used before.
    // User wants difference color per box.
    val baseColor = tileColor

    val backgroundColor =
        if (tile.isTapped) Color.DarkGray.copy(alpha = 0.2f) else baseColor.copy(alpha = 0.15f)
    val borderColor = if (isHighlighted) Color.White else baseColor
    val glowColor = if (isHighlighted) Color.White else baseColor

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isHighlighted) 3.dp else 2.dp,
                color = borderColor.copy(alpha = if (tile.isTapped) 0.3f else 1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !tile.isTapped) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!tile.isTapped) {
            NeonText(
                text = tile.number.toString(),
                fontSize = 20,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                hasShadow = true,
                autoResize = true,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun NeonAssistButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeonButton(
        text = label,
        onClick = onClick,
        color = color,
        icon = icon,
        height = 40,
        fontSize = 9,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GamePlayScreenPreview() {
    val mockTiles = (1..9).map { i ->
        Tile(i, i, Color.White, isTapped = i == 2)
    }
    NumriseTheme {
        GamePlayContent(
            tiles = mockTiles,
            gridSize = 3,
            timerText = "00:45",
            score = 1200500,
            coins = 150,
            highlightedTileId = 3,
            onTileClick = {},
            onShop = {},
            onHint = {},
            onUndo = {}
        )
    }
}
