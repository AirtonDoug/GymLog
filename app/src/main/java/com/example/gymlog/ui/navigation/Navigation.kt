package com.example.gymlog.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    // Create a shared repository instance for all ViewModels
    val repository = MockWorkoutRepository()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // Let HomeScreen create its own ViewModel
            HomeScreen(navController = navController)
        }

        composable("log") {
            // Let LogScreen create its own ViewModel
            LogScreen(navController = navController)
        }

        composable("profile") {
            // Let ProfileScreen create its own ViewModel
            ProfileScreen(navController = navController)
        }

        composable("start_workout") {
            // Let StartWorkoutScreen create its own ViewModel
            StartWorkoutScreen(navController = navController)
        }

        composable(
            route = "active_workout/{workoutIdOrCustom}",
            arguments = listOf(navArgument("workoutIdOrCustom") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutIdOrCustom = backStackEntry.arguments?.getString("workoutIdOrCustom") ?: "custom"

            // Pass only the required parameters
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

            // Let WorkoutDetailScreen create its own ViewModel
            WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId
            )
        }

        composable("favorites") {
            // Let FavoritesScreen create its own ViewModel
            FavoritesScreen(navController = navController)
        }

        composable("settings") {
            // Pass only the required parameters
            SettingsScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onClearFavorites = { /* Will be handled by ViewModel */ },
                onResetPreferences = { /* Will be handled by ViewModel */ }
            )
        }

        composable("search") {
            // Create SearchViewModel with factory
            val searchViewModel: SearchViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SearchViewModel(repository) as T
                    }
                }
            )

            // Collect state from ViewModel
            val uiState by searchViewModel.uiState.collectAsState()

            // Pass all required parameters to SearchResultsScreen
            SearchResultsScreen(
                navController = navController,
                searchQuery = uiState.searchQuery,
                searchResults = uiState.searchResults,
                onSearchQueryChange = { searchViewModel.onSearchQueryChange(it) },
                favoriteRoutineIds = uiState.searchResults.filter { it.isFavorite }.map { it.id }.toSet(),
                onToggleFavorite = { searchViewModel.toggleFavorite(it) }
            )
        }

        composable("help") {
            // Let HelpScreen create its own ViewModel
            HelpScreen(navController = navController)
        }
    }
}
