package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.WorkoutRoutine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI State for Favorites Screen
data class FavoritesUiState(
    val favoriteRoutines: List<WorkoutRoutine> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class FavoritesViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = workoutRepository.getFavoriteWorkoutRoutines()
        .map { routines -> FavoritesUiState(favoriteRoutines = routines, isLoading = false) }
        .catch { e ->
            emit(FavoritesUiState(isLoading = false, errorMessage = "Failed to load favorites: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState(isLoading = true)
        )

    fun removeFavorite(routine: WorkoutRoutine) {
        viewModelScope.launch {
            try {
                // Toggle favorite will remove it if it's already a favorite
                workoutRepository.toggleFavorite(routine.id)
            } catch (e: Exception) {
                // Handle error
                // Consider updating UI state with an error message
            }
        }
    }
}
