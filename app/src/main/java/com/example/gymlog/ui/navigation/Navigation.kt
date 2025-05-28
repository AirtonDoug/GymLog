package com.example.gymlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymlog.models.WorkoutItem
import com.example.gymlog.models.mockWorkouts
import com.example.gymlog.ui.screens.FavoritesScreen
import com.example.gymlog.ui.screens.HelpScreen
import com.example.gymlog.ui.screens.HomeScreen
import com.example.gymlog.ui.screens.SearchResultsScreen
import com.example.gymlog.ui.screens.SettingsScreen
import com.example.gymlog.ui.screens.WorkoutDetailScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    // Estado compartilhado entre telas
    var searchQuery by remember { mutableStateOf("") }
    var isDarkTheme by remember { mutableStateOf(false) }
    var favoriteWorkouts by remember { mutableStateOf(mockWorkouts.filter { it.isFavorite }) }

    // Funções de gerenciamento de estado
    val onSearchQueryChange: (String) -> Unit = { query ->
        searchQuery = query
    }

    val onThemeToggle: () -> Unit = {
        isDarkTheme = !isDarkTheme
    }

    val onToggleFavorite: (WorkoutItem) -> Unit = { workout ->
        val updatedWorkout = workout.copy(isFavorite = !workout.isFavorite)
        favoriteWorkouts = if (updatedWorkout.isFavorite) {
            favoriteWorkouts + updatedWorkout
        } else {
            favoriteWorkouts.filter { it.id != workout.id }
        }
    }

    val onClearFavorites: () -> Unit = {
        favoriteWorkouts = emptyList()
    }

    val onResetPreferences: () -> Unit = {
        isDarkTheme = false
        // Outras preferências podem ser redefinidas aqui
    }

    NavHost(navController = navController, startDestination = "home") {
        // Tela Inicial
        composable("home") {
            HomeScreen(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        // Tela de Detalhes do Treino
        composable(
            route = "workout_details/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: 1
            WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId,
                onToggleFavorite = onToggleFavorite
            )
        }

        // Tela de Favoritos
        composable("favorites") {
            FavoritesScreen(
                navController = navController,
                favoriteWorkouts = favoriteWorkouts,
                onRemoveFavorite = { workout ->
                    onToggleFavorite(workout)
                }
            )
        }

        // Tela de Configurações
        composable("settings") {
            SettingsScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onClearFavorites = onClearFavorites,
                onResetPreferences = onResetPreferences
            )
        }

        // Tela de Resultados de Busca
        composable("search") {
            val searchResults = if (searchQuery.isEmpty()) {
                emptyList()
            } else {
                mockWorkouts.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true) ||
                            it.difficulty.contains(searchQuery, ignoreCase = true)
                }
            }

            SearchResultsScreen(
                navController = navController,
                searchQuery = searchQuery,
                searchResults = searchResults,
                onSearchQueryChange = onSearchQueryChange
            )
        }

        // Tela de Ajuda e Suporte
        composable("help") {
            HelpScreen(
                navController = navController
            )
        }
    }
}
