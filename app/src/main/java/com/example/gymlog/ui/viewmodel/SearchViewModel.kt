package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.WorkoutRoutine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI State for Search Screen
data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<WorkoutRoutine> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SearchViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    // Mutable state for search query
    private val _searchQuery = MutableStateFlow("")

    // Expose UI state as StateFlow
    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery,
        workoutRepository.getWorkoutRoutines()
    ) { query, routines ->
        val filteredRoutines = if (query.isBlank()) {
            emptyList() // Don't show results until user types something
        } else {
            routines.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.difficulty.contains(query, ignoreCase = true)
            }
        }

        SearchUiState(
            searchQuery = query,
            searchResults = filteredRoutines,
            isLoading = false
        )
    }.catch { e ->
        emit(SearchUiState(errorMessage = "Failed to search: ${e.message}"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isLoading = true)
    )

    // Update search query
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Toggle favorite status for a routine
    fun toggleFavorite(routine: WorkoutRoutine) {
        viewModelScope.launch {
            try {
                workoutRepository.toggleFavorite(routine.id)
                // UI will update automatically via the StateFlow
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}

// Factory for creating SearchViewModel
class SearchViewModelFactory(private val repository: WorkoutRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
