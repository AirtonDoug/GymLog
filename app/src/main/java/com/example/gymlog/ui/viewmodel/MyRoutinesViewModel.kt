package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.WorkoutRoutineWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class MyRoutinesUiState(
    val routines: List<WorkoutRoutineWithExercises> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MyRoutinesViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MyRoutinesUiState())
    val uiState: StateFlow<MyRoutinesUiState> = _uiState.asStateFlow()

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            workoutRepository.getWorkoutRoutines()
                .onStart { _uiState.value = MyRoutinesUiState(isLoading = true) }
                .catch { e ->
                    _uiState.value = MyRoutinesUiState(errorMessage = e.message)
                }
                .collect { routines ->
                    _uiState.value = MyRoutinesUiState(routines = routines)
                }
        }
    }

    fun deleteRoutine(routineId: Int) {
        viewModelScope.launch {
            workoutRepository.deleteWorkoutRoutine(routineId)
        }
    }
}
