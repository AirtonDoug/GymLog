package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.models.WorkoutRoutineWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CreateEditRoutineUiState(
    val routine: WorkoutRoutine = WorkoutRoutine(0, "", "", 0, "", "", 0),
    val exercises: List<Exercise> = emptyList(),
    val allExercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false
)

class CreateEditRoutineViewModel(
    private val workoutRepository: WorkoutRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEditRoutineUiState())
    val uiState: StateFlow<CreateEditRoutineUiState> = _uiState.asStateFlow()

    private val routineId: Int? = savedStateHandle.get<Int>("routineId")

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val allExercises = workoutRepository.getAllExercises().first()
            if (routineId != null && routineId != 0) {
                val routineWithExercises = workoutRepository.getWorkoutRoutineById(routineId).first()
                if (routineWithExercises != null) {
                    _uiState.value = CreateEditRoutineUiState(
                        routine = routineWithExercises.routine,
                        exercises = routineWithExercises.exercises,
                        allExercises = allExercises,
                        isLoading = false
                    )
                } else {
                    _uiState.value = CreateEditRoutineUiState(
                        allExercises = allExercises,
                        isLoading = false,
                        errorMessage = "Routine not found"
                    )
                }
            } else {
                _uiState.value = CreateEditRoutineUiState(
                    allExercises = allExercises,
                    isLoading = false
                )
            }
        }
    }

    fun onRoutineNameChange(name: String) {
        _uiState.value = _uiState.value.copy(routine = _uiState.value.routine.copy(name = name))
    }

    fun onRoutineDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(routine = _uiState.value.routine.copy(description = description))
    }

    fun addExercise(exercise: Exercise) {
        val currentExercises = _uiState.value.exercises.toMutableList()
        if (!currentExercises.contains(exercise)) {
            currentExercises.add(exercise)
            _uiState.value = _uiState.value.copy(exercises = currentExercises)
        }
    }

    fun removeExercise(exercise: Exercise) {
        val currentExercises = _uiState.value.exercises.toMutableList()
        currentExercises.remove(exercise)
        _uiState.value = _uiState.value.copy(exercises = currentExercises)
    }

    fun saveRoutine() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val routineWithExercises = WorkoutRoutineWithExercises(
                routine = _uiState.value.routine,
                exercises = _uiState.value.exercises
            )
            // I need a method in the repository to save a routine with exercises
            // workoutRepository.saveWorkoutRoutine(routineWithExercises)
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }
}
