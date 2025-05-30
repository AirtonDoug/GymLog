package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.WorkoutRoutine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI State for Workout Detail Screen
data class WorkoutDetailUiState(
    val routine: WorkoutRoutine? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class WorkoutDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // Get workoutId from navigation arguments
    private val workoutId: StateFlow<Int> = savedStateHandle.getStateFlow("workoutId", -1)

    // Combine the workout details flow with the specific workoutId
    val uiState: StateFlow<WorkoutDetailUiState> = workoutId
        .filter { it != -1 } // Only proceed if workoutId is valid
        .flatMapLatest { id ->
            // Fetch the specific routine based on the ID
            workoutRepository.getWorkoutRoutineById(id)
        }
        .map { routine ->
            if (routine != null) {
                WorkoutDetailUiState(
                    routine = routine,
                    isFavorite = routine.isFavorite, // isFavorite is updated by the repository flow
                    isLoading = false
                )
            } else {
                WorkoutDetailUiState(
                    isLoading = false,
                    errorMessage = "Routine not found."
                )
            }
        }
        .catch { e ->
            emit(WorkoutDetailUiState(isLoading = false, errorMessage = "Failed to load routine details: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkoutDetailUiState(isLoading = true)
        )

    fun toggleFavorite() {
        val currentRoutine = uiState.value.routine
        if (currentRoutine != null) {
            viewModelScope.launch {
                try {
                    workoutRepository.toggleFavorite(currentRoutine.id)
                    // UI state will update automatically via the flow from the repository
                } catch (e: Exception) {
                    // Handle error, maybe update UI state
                    // _uiState.update { it.copy(errorMessage = "Failed to update favorite status: ${e.message}") }
                }
            }
        }
    }
}
