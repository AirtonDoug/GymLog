package com.example.gymlog.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymlog.data.repositories.MockWorkoutRepository // Temporary for factories
import com.example.gymlog.ui.screens.*
import com.example.gymlog.ui.viewmodel.*

// Basic ViewModel Factory (replace with proper DI later)
// You might want to move these factories to a dedicated file or use a DI framework
class FavoritesViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            // In a real app, use Hilt or another DI framework to provide the repository
            return FavoritesViewModel(MockWorkoutRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WorkoutDetailViewModelFactory(private val workoutId: Int) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutDetailViewModel::class.java)) {
            // In a real app, you would pass SavedStateHandle here,
            // but viewModel() handles it. We pass the ID differently for simplicity here.
            // A better approach uses Hilt or manual SavedStateHandle injection.
            val fakeSavedStateHandle = SavedStateHandle(mapOf("workoutId" to workoutId))
            return WorkoutDetailViewModel(fakeSavedStateHandle, MockWorkoutRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    // Theme state is still managed at a higher level (MainActivity/MainApp)
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    // State like searchQuery and favorites is now managed within ViewModels.
    // No need for remember { mutableStateOf(...) } here for that.

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // HomeScreen gets its ViewModel via viewModel()
            HomeScreen(navController = navController)
            // Theme props could be passed if needed for components outside ViewModel scope
            // isDarkTheme = isDarkTheme,
            // onThemeToggle = onThemeToggle
        }

        composable("log") {
            // TODO: Create LogViewModel and refactor LogScreen
            LogScreen(navController = navController)
        }

        composable("profile") {
            // TODO: Create ProfileViewModel and refactor ProfileScreen
            ProfileScreen(navController = navController)
        }

        composable("start_workout") {
            // TODO: Create StartWorkoutViewModel and refactor StartWorkoutScreen
            StartWorkoutScreen(navController = navController)
        }

        composable(
            route = "active_workout/{workoutIdOrCustom}",
            arguments = listOf(navArgument("workoutIdOrCustom") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutIdOrCustom = backStackEntry.arguments?.getString("workoutIdOrCustom") ?: "custom"
            // TODO: Create ActiveWorkoutViewModel and refactor ActiveWorkoutScreen
            ActiveWorkoutScreen(navController = navController, workoutIdOrCustom = workoutIdOrCustom)
        }

        composable(
            route = "workout_details/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            // WorkoutDetailScreen gets its ViewModel via viewModel(),
            // which automatically handles SavedStateHandle for arguments.
            // TODO: Implement WorkoutDetailScreen properly with ViewModel
            WorkoutDetailScreen(
                navController = navController,
                workoutRoutine = TODO(), // Needs data from WorkoutDetailViewModel
                isFavorite = TODO(),     // Needs data from WorkoutDetailViewModel
                onToggleFavorite = TODO(), // Needs action from WorkoutDetailViewModel
                workoutId = backStackEntry.arguments?.getInt("workoutId") ?: -1 // Pass the ID
            )
        }

        composable("favorites") {
            // Instantiate FavoritesViewModel using the factory (or Hilt in a real app)
            val favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModelFactory())
            // Collect the UI state from the ViewModel
            val uiState by favoritesViewModel.uiState.collectAsState()

            // Pass the collected state and the remove function to the screen
            FavoritesScreen(
                navController = navController,
                favoriteWorkouts = uiState.favoriteRoutines, // Pass the list from state
                onRemoveFavorite = { routine -> favoritesViewModel.removeFavorite(routine) } // Pass the remove function
            )
        }

        composable("settings") {
            // SettingsScreen might need its own ViewModel too, especially for clearing favorites
            // For now, passing callbacks might be acceptable if logic is simple
            // TODO: Create SettingsViewModel and refactor SettingsScreen
            // val settingsViewModel: SettingsViewModel = viewModel(...)
            SettingsScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                // TODO: Replace these with ViewModel actions
                onClearFavorites = { /* settingsViewModel.clearFavorites() */ },
                onResetPreferences = { /* settingsViewModel.resetPreferences() */ }
            )
        }

        composable("search") {
            // Search results can be part of HomeViewModel or a dedicated SearchViewModel
            // Reusing HomeViewModel state for simplicity here
            // val homeViewModel: HomeViewModel = viewModel() // Get potentially shared VM
            // val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            // val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()

            // TODO: Refactor SearchResultsScreen to use ViewModel
            /* SearchResultsScreen(
                navController = navController,
                searchQuery = searchQuery,
                searchResults = uiState.routines, // Use routines from HomeViewModel state
                onSearchQueryChange = { homeViewModel.onSearchQueryChange(it) },
                favoriteRoutineIds = uiState.routines.filter { it.isFavorite }.map { it.id }.toSet(), // Derive from state
                onToggleFavorite = { homeViewModel.toggleFavorite(it) }
            ) */
            Column (Modifier.padding(16.dp)) { Text("Tela de Busca (Refatoração Pendente)") }
        }

        composable("help") {
            // TODO: Create HelpViewModel and refactor HelpScreen if needed
            HelpScreen(navController = navController)
        }
    }
}

