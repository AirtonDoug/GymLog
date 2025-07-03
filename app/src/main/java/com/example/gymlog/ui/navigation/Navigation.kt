package com.example.gymlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymlog.data.repositories.MockWorkoutRepository
import com.example.gymlog.ui.screens.*
import com.example.gymlog.ui.viewmodel.SearchViewModel

@Composable
fun AppNavigation(
    navController: NavHostController
    // Os parâmetros isDarkTheme e onThemeToggle foram removidos
) {
    // Cria uma instância do repositório para ser compartilhada (em um app real, use injeção de dependência)
    val repository = MockWorkoutRepository()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("log") {
            LogScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("start_workout") {
            StartWorkoutScreen(navController = navController)
        }

        composable(
            route = "active_workout/{workoutIdOrCustom}",
            arguments = listOf(navArgument("workoutIdOrCustom") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutIdOrCustom = backStackEntry.arguments?.getString("workoutIdOrCustom") ?: "custom"
            ActiveWorkoutScreen(
                navController = navController,
                workoutIdOrCustom = workoutIdOrCustom
            )
        }

        composable(
            route = "workout_details/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: -1
            WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId
            )
        }

        composable("favorites") {
            FavoritesScreen(navController = navController)
        }

        composable("settings") {
            // A chamada para SettingsScreen agora é mais simples,
            // pois ela gerencia o próprio estado de tema.
            SettingsScreen(
                navController = navController
            )
        }

        composable("search") {
            val searchViewModel: SearchViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SearchViewModel(repository) as T
                    }
                }
            )
            val uiState by searchViewModel.uiState.collectAsState()

            SearchResultsScreen(
                navController = navController,
                searchQuery = uiState.searchQuery,
                searchResults = uiState.searchResults,
                isLoading = uiState.isLoading,
                onSearchQueryChange = { searchViewModel.onSearchQueryChange(it) },
                favoriteRoutineIds = uiState.searchResults.filter { it.isFavorite }.map { it.id }.toSet(),
                onToggleFavorite = { searchViewModel.toggleFavorite(it) }
            )
        }

        composable("help") {
            HelpScreen(navController = navController)
        }
    }
}
