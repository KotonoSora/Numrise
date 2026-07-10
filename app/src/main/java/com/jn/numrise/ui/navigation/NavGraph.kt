package com.jn.numrise.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jn.numrise.ui.screens.CoinShopScreen
import com.jn.numrise.ui.screens.DifficultySelectScreen
import com.jn.numrise.ui.screens.GamePlayScreen
import com.jn.numrise.ui.screens.HelpScreen
import com.jn.numrise.ui.screens.HomeScreen
import com.jn.numrise.ui.screens.LevelSelectScreen
import com.jn.numrise.ui.screens.PauseScreen
import com.jn.numrise.ui.screens.ResultScreen
import com.jn.numrise.ui.screens.SettingsScreen
import com.jn.numrise.viewmodel.GameIntent
import com.jn.numrise.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DifficultySelect : Screen("difficulty_select")
    object GamePlay : Screen("game_play")
    object Pause : Screen("pause")
    object Result : Screen("result")
    object LevelSelect : Screen("level_select")
    object CoinShop : Screen("coin_shop")
    object Settings : Screen("settings")
    object Help : Screen("help")
}

@Composable
fun NumriseNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues,
    gameViewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(contentPadding)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(Screen.DifficultySelect.route) {
            DifficultySelectScreen(
                onDifficultySelected = { difficulty ->
                    gameViewModel.onIntent(GameIntent.StartWithDifficulty(difficulty))
                    navController.navigate(Screen.GamePlay.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GamePlay.route) {
            GamePlayScreen(
                viewModel = gameViewModel,
                onPause = { navController.navigate(Screen.Pause.route) },
                onFinished = { navController.navigate(Screen.Result.route) }
            )
        }

        composable(Screen.Pause.route) {
            PauseScreen(
                onResume = {
                    gameViewModel.onIntent(GameIntent.ResumeGame)
                    navController.popBackStack()
                },
                onRestart = {
                    gameViewModel.onIntent(GameIntent.RestartGame)
                    navController.popBackStack()
                },
                onQuit = {
                    gameViewModel.onIntent(GameIntent.ResetToIdle)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            ResultScreen(
                score = uiState.score,
                time = uiState.timerFormatted,
                onNextLevel = {
                    gameViewModel.onIntent(GameIntent.ResetToIdle)
                    navController.navigate(Screen.DifficultySelect.route) {
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
                }
            )
        }

        composable(Screen.LevelSelect.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            LevelSelectScreen(
                levels = uiState.levels,
                onLevelSelected = { level ->
                    gameViewModel.onIntent(GameIntent.StartWithLevel(level))
                    navController.navigate(Screen.GamePlay.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CoinShop.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            CoinShopScreen(
                coins = uiState.coins,
                onBuyPack = { pack -> gameViewModel.onIntent(GameIntent.BuyCoins(pack.amount)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
