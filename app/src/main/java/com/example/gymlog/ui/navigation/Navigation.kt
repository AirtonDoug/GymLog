package com.example.gymlog.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymlog.models.WorkoutRoutine // Changed from WorkoutItem
import com.example.gymlog.models.mockWorkoutRoutines // Changed from mockWorkouts
import com.example.gymlog.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    isDarkTheme: Boolean, // Receive theme state from MainApp
    onThemeToggle: () -> Unit // Receive toggle function from MainApp
) {
    // Estado compartilhado entre telas (exceto tema)
    var searchQuery by remember { mutableStateOf("") }
    // Use WorkoutRoutine for favorites now, assuming routines can be favorited
    var favoriteWorkouts by remember { mutableStateOf(mockWorkoutRoutines.filter { it.isFavorite }) }

    // Funções de gerenciamento de estado (exceto tema)
    val onSearchQueryChange: (String) -> Unit = { query ->
        searchQuery = query
    }

    // Adapt favorite toggle for WorkoutRoutine
    val onToggleFavorite: (WorkoutRoutine) -> Unit = { routine ->
        val updatedRoutine = routine.copy(isFavorite = !routine.isFavorite)
        // Update the source list if it's mutable or manage state appropriately
        // For mock data, we might need to update the mockWorkoutRoutines list or handle state differently
        favoriteWorkouts = if (updatedRoutine.isFavorite) {
            (favoriteWorkouts + updatedRoutine).distinctBy { it.id }
        } else {
            favoriteWorkouts.filter { it.id != routine.id }
        }
        // Update the main list as well (important for consistency)
        val index = mockWorkoutRoutines.indexOfFirst { it.id == routine.id }
        if (index != -1) {
            // This modification assumes mockWorkoutRoutines is mutable or handled via state
            // In a real app, update the data source (DB, API)
            // mockWorkoutRoutines[index] = updatedRoutine // This won't work if mockWorkoutRoutines is immutable List
        }
    }

    val onClearFavorites: () -> Unit = {
        favoriteWorkouts = emptyList()
        // Also update the main list if necessary
        mockWorkoutRoutines.forEach { it.isFavorite = false } // Example for mock data
    }

    val onResetPreferences: () -> Unit = {
        // Reset other preferences if needed, theme is handled separately
    }

    NavHost(navController = navController, startDestination = "home") {
        // Tela Inicial (Shows WorkoutRoutines now)
        composable("home") {
            // HomeScreen might need adaptation if it was showing WorkoutItem before
            HomeScreen(
                navController = navController,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        // Tela de Log (Registro e Histórico)
        composable("log") {
            LogScreen(navController = navController)
        }

        // Tela de Perfil
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Tela para Iniciar um Treino (Selecionar Rotina ou Personalizado)
        composable("start_workout") {
            StartWorkoutScreen(navController = navController)
        }

        // Tela de Treino Ativo (Registrando o Treino)
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

        // Tela de Detalhes da Rotina (Adapt if needed)
        composable(
            route = "workout_details/{workoutId}", // Assuming this now shows Routine details
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: 1
            // WorkoutDetailScreen might need adaptation for WorkoutRoutine
            // Or create a new RoutineDetailScreen
            /* WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId,
                onToggleFavorite = { /* Adapt for Routine */ }
            ) */
            // Placeholder: Navigate back or show simple text until adapted
            Column(Modifier.padding(16.dp)) { Text("Detalhes da Rotina ID: $workoutId (Adaptação Pendente)") }
        }

        // Tela de Favoritos (Shows WorkoutRoutines now)
        composable("favorites") {
            // FavoritesScreen needs adaptation for WorkoutRoutine
            /* FavoritesScreen(
                navController = navController,
                favoriteWorkouts = favoriteWorkouts, // Pass list of WorkoutRoutine
                onRemoveFavorite = { routine -> onToggleFavorite(routine) }
            ) */
            // Placeholder: Navigate back or show simple text until adapted
            Column(Modifier.padding(16.dp)) { Text("Favoritos (Adaptação Pendente)") }
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

        // Tela de Resultados de Busca (Adapt for WorkoutRoutine)
        composable("search") {
            val searchResults = if (searchQuery.isEmpty()) {
                emptyList()
            } else {
                mockWorkoutRoutines.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true) ||
                            it.difficulty.contains(searchQuery, ignoreCase = true)
                }
            }
            // SearchResultsScreen needs adaptation for WorkoutRoutine
            /* SearchResultsScreen(
                navController = navController,
                searchQuery = searchQuery,
                searchResults = searchResults, // Pass list of WorkoutRoutine
                onSearchQueryChange = onSearchQueryChange
            ) */
            // Placeholder: Navigate back or show simple text until adapted
            Column(Modifier.padding(16.dp)) { Text("Resultados da Busca (Adaptação Pendente)") }
        }

        // Tela de Ajuda e Suporte
        composable("help") {
            HelpScreen(navController = navController)
        }
    }
}
