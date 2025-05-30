package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.WorkoutRoutine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Data class to hold the UI state for the Home screen
data class HomeUiState(
    val routines: List<WorkoutRoutine> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combine routines flow with search query flow to get filtered results
    val uiState: StateFlow<HomeUiState> = combine(
        workoutRepository.getWorkoutRoutines(), // Gets routines with updated favorite status
        _searchQuery
    ) { routines, query ->
        val filteredRoutines = if (query.isBlank()) {
            routines
        } else {
            routines.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.difficulty.contains(query, ignoreCase = true)
            }
        }
        HomeUiState(
            routines = filteredRoutines,
            searchQuery = query,
            isLoading = false // Assuming loading is done once routines are emitted
        )
    }.catch { e ->
        // Handle errors, e.g., update state with error message
        emit(HomeUiState(isLoading = false, errorMessage = "Failed to load routines: ${e.message}"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep flow active for 5s after last subscriber
        initialValue = HomeUiState(isLoading = true) // Initial loading state
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(routine: WorkoutRoutine) {
        viewModelScope.launch {
            try {
                workoutRepository.toggleFavorite(routine.id)
                // StateFlow will automatically update the UI as getWorkoutRoutines() emits new list
            } catch (e: Exception) {
                // Handle error, maybe update UI state with an error message
                // _uiState.update { it.copy(errorMessage = "Failed to update favorite: ${e.message}") }
            }
        }
    }
}
