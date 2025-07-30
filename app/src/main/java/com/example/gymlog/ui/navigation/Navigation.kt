package com.example.gymlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.gymlog.data.database.GymLogDatabase
import com.example.gymlog.data.repositories.UserPreferencesRepository
import com.example.gymlog.data.repositories.WorkoutRepositoryImpl
import com.example.gymlog.ui.screens.*
import com.example.gymlog.ui.viewmodel.FavoritesViewModel
import com.example.gymlog.ui.viewmodel.HomeViewModel
import com.example.gymlog.ui.viewmodel.CreateEditRoutineViewModel
import com.example.gymlog.ui.viewmodel.MyRoutinesViewModel
import com.example.gymlog.ui.viewmodel.SearchViewModel
import com.example.gymlog.ui.viewmodel.WorkoutDetailViewModel

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current
    // Cria uma única instância do repositório para ser compartilhada
    val repository = remember {
        val db = GymLogDatabase.getDatabase(context)
        WorkoutRepositoryImpl(
            workoutRoutineDao = db.workoutRoutineDao(),
            exerciseDao = db.exerciseDao(),
            workoutLogDao = db.workoutLogDao(),
            profileDao = db.profileDao(),
            userPreferencesRepository = UserPreferencesRepository.getInstance(context)
        )
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(repository) as T
                    }
                }
            )
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }

        composable("log") {
            LogScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("start_workout") {
            val startWorkoutViewModel: StartWorkoutViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return StartWorkoutViewModel(repository) as T
                    }
                }
            )
            StartWorkoutScreen(
                navController = navController,
                startWorkoutViewModel = startWorkoutViewModel
            )
        }

        composable(
            route = "active_workout/{workoutIdOrCustom}",
            arguments = listOf(navArgument("workoutIdOrCustom") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutIdOrCustom = backStackEntry.arguments?.getString("workoutIdOrCustom") ?: "custom"
            val activeWorkoutViewModel: ActiveWorkoutViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val savedStateHandle = SavedStateHandle(mapOf("workoutIdOrCustom" to workoutIdOrCustom))
                        return ActiveWorkoutViewModel(savedStateHandle, repository) as T
                    }
                }
            )
            ActiveWorkoutScreen(
                navController = navController,
                activeWorkoutViewModel = activeWorkoutViewModel
            )
        }

        composable(
            route = "workout_details/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: -1
            val detailViewModel: WorkoutDetailViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val savedStateHandle = SavedStateHandle(mapOf("workoutId" to workoutId))
                        return WorkoutDetailViewModel(savedStateHandle, repository) as T
                    }
                }
            )
            WorkoutDetailScreen(
                navController = navController,
                workoutId = workoutId,
                detailViewModel = detailViewModel
            )
        }

        composable("favorites") {
            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return FavoritesViewModel(repository) as T
                    }
                }
            )
            FavoritesScreen(navController = navController, favoritesViewModel = favoritesViewModel)
        }

        composable("settings") {
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
            val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

            SearchResultsScreen(
                navController = navController,
                searchQuery = uiState.searchQuery,
                searchResults = uiState.searchResults,
                isLoading = uiState.isLoading,
                onSearchQueryChange = { searchViewModel.onSearchQueryChange(it) },
                favoriteRoutineIds = uiState.searchResults.filter { it.routine.isFavorite }.map { it.routine.id }.toSet(),
                onToggleFavorite = { searchViewModel.toggleFavorite(it) }
            )
        }

        composable("help") {
            HelpScreen(navController = navController)
        }
        composable("my_routines") {
            val myRoutinesViewModel: MyRoutinesViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MyRoutinesViewModel(repository) as T
                    }
                }
            )
            MyRoutinesScreen(navController = navController, myRoutinesViewModel = myRoutinesViewModel)
        }
        composable("create_routine") {
            navController.navigate("create_edit_routine/0")
        }
        composable(
            "create_edit_routine/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId") ?: 0
            val createEditRoutineViewModel: CreateEditRoutineViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val savedStateHandle = SavedStateHandle(mapOf("routineId" to routineId))
                        return CreateEditRoutineViewModel(repository, savedStateHandle) as T
                    }
                }
            )
            CreateEditRoutineScreen(
                navController = navController,
                createEditRoutineViewModel = createEditRoutineViewModel
            )
        }
    }
}