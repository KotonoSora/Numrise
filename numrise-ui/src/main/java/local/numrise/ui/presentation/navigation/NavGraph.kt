package local.numrise.ui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import local.numrise.engine.domain.model.GameStatus
import local.numrise.ui.presentation.screens.CoinShopScreen
import local.numrise.ui.presentation.screens.DailyChallengeScreen
import local.numrise.ui.presentation.screens.DifficultySelectScreen
import local.numrise.ui.presentation.screens.GamePlayScreen
import local.numrise.ui.presentation.screens.HelpScreen
import local.numrise.ui.presentation.screens.HomeScreen
import local.numrise.ui.presentation.screens.LeaderboardScreen
import local.numrise.ui.presentation.screens.ResultScreen
import local.numrise.ui.presentation.screens.SettingsScreen
import local.numrise.ui.presentation.viewmodel.GameIntent
import local.numrise.ui.presentation.viewmodel.GameViewModel
import local.numrise.ui.presentation.viewmodel.SettingsViewModel
import local.numrise.ui.presentation.viewmodel.ShopViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DifficultySelect : Screen("difficulty_select")
    object GamePlay : Screen("game_play")
    object Result : Screen("result")
    object CoinShop : Screen("coin_shop")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object Leaderboard : Screen("leaderboard")
    object DailyChallenge : Screen("daily_challenge")
}

@Composable
fun NumriseNavGraph(
    gameViewModel: GameViewModel,
    shopViewModel: ShopViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            HomeScreen(
                coins = uiState.coins,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.DifficultySelect.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            DifficultySelectScreen(
                coins = uiState.coins,
                onDifficultySelected = { difficulty ->
                    gameViewModel.onIntent(GameIntent.StartWithDifficulty(difficulty))
                    navController.navigate(Screen.GamePlay.route)
                },
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.GamePlay.route) {
            GamePlayScreen(
                viewModel = gameViewModel,
                onFinished = { navController.navigate(Screen.Result.route) },
                onShop = { navController.navigate(Screen.CoinShop.route) }
            )
        }

        composable(Screen.Result.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            ResultScreen(
                coins = uiState.coins,
                score = uiState.score,
                time = uiState.timerFormatted,
                reward = uiState.lastReward,
                isWin = uiState.status == GameStatus.FINISHED,
                onNextLevel = {
                    gameViewModel.onIntent(GameIntent.StartNext)
                    navController.navigate(Screen.GamePlay.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onRestart = {
                    gameViewModel.onIntent(GameIntent.RestartGame)
                    navController.navigate(Screen.GamePlay.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHome = {
                    gameViewModel.onIntent(GameIntent.ResetToIdle)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onShop = { navController.navigate(Screen.CoinShop.route) }
            )
        }

        composable(Screen.CoinShop.route) {
            CoinShopScreen(
                viewModel = shopViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                shopViewModel = shopViewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Help.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            HelpScreen(
                coins = uiState.coins,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Leaderboard.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            LeaderboardScreen(
                coins = uiState.coins,
                history = uiState.history,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.DailyChallenge.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            DailyChallengeScreen(
                coins = uiState.coins,
                onPlay = {
                    gameViewModel.onIntent(GameIntent.StartDailyChallenge)
                    navController.navigate(Screen.GamePlay.route)
                },
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}
