package local.numrise.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.numrise.ui.presentation.components.NeonHeaderBar
import local.numrise.ui.presentation.components.NeonText
import local.numrise.ui.presentation.navigation.Screen
import local.numrise.ui.presentation.theme.NeonPink
import local.numrise.ui.presentation.theme.NumriseTheme
import local.numrise.ui.presentation.viewmodel.SettingsViewModel
import local.numrise.ui.presentation.viewmodel.ShopViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    shopViewModel: ShopViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()
    val coins by shopViewModel.coins.collectAsState()

    SettingsContent(
        coins = coins,
        soundEnabled = settings.soundEnabled,
        onSoundToggle = { settingsViewModel.toggleSound(it) },
        onBack = onBack,
        onShop = { onNavigate(Screen.CoinShop.route) }
    )
}

@Composable
fun SettingsContent(
    coins: Int,
    soundEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    onBack: () -> Unit,
    onShop: () -> Unit
) {
    Scaffold(
        topBar = {
            NeonHeaderBar(
                title = "SETTINGS",
                coins = coins,
                onBack = onBack,
                onShop = onShop
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NeonSettingsToggle(
                label = "SOUND EFFECTS",
                checked = soundEnabled,
                color = NeonPink
            ) {
                onSoundToggle(it)
            }
        }
    }
}

@Composable
fun NeonSettingsToggle(
    label: String,
    checked: Boolean,
    color: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonText(
            text = label,
            fontSize = 12,
            color = Color.White,
            modifier = Modifier.weight(1f),
            autoResize = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color,
                checkedTrackColor = color.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    NumriseTheme {
        SettingsContent(
            coins = 1200,
            soundEnabled = true,
            onSoundToggle = {},
            onBack = {},
            onShop = {}
        )
    }
}
