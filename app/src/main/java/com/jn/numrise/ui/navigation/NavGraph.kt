package com.jn.numrise.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.numrise.di.LocalAppContainer
import com.jn.numrise.domain.model.GameStatus
import com.jn.numrise.ui.screens.CoinShopScreen
import com.jn.numrise.ui.screens.DailyChallengeScreen
import com.jn.numrise.ui.screens.DifficultySelectScreen
import com.jn.numrise.ui.screens.GamePlayScreen
import com.jn.numrise.ui.screens.HelpScreen
import com.jn.numrise.ui.screens.HomeScreen
import com.jn.numrise.ui.screens.LeaderboardScreen
import com.jn.numrise.ui.screens.ResultScreen
import com.jn.numrise.ui.screens.SettingsScreen
import com.jn.numrise.viewmodel.CoinShopViewModel
import com.jn.numrise.viewmodel.GameIntent
import com.jn.numrise.viewmodel.GameViewModel
import com.jn.numrise.viewmodel.GameViewModelFactory

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
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = LocalAppContainer.current

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
            val coinShopViewModel: CoinShopViewModel = viewModel(
                factory = GameViewModelFactory(container)
            )
            val uiState by gameViewModel.uiState.collectAsState()
            val packs by coinShopViewModel.coinPacks.collectAsState()

            CoinShopScreen(
                coins = uiState.coins,
                packs = packs,
                onBuyPack = { pack ->
                    coinShopViewModel.buyPack(context as Activity, pack)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = gameViewModel,
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
