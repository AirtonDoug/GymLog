package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.repositories.WorkoutRepository
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.PerformedExercise
import com.example.gymlog.models.PerformedExerciseWithSets
import com.example.gymlog.models.PerformedSet
import com.example.gymlog.models.WorkoutLogEntry
import com.example.gymlog.models.WorkoutLogEntryWithExercises
import com.example.gymlog.models.WorkoutRoutineWithExercises
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

// UI State for ActiveWorkout Screen
data class ActiveWorkoutUiState(
    val workoutRoutine: WorkoutRoutineWithExercises? = null,
    val isCustomWorkout: Boolean = false,
    val currentExercises: List<PerformedExercise> = emptyList(),
    val currentSets: Map<String, List<PerformedSet>> = emptyMap(),
    val allExercises: List<Exercise> = emptyList(),
    val timerRunning: Boolean = false,
    val timerSeconds: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ActiveWorkoutViewModel(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // Get workoutIdOrCustom from navigation arguments
    private val workoutIdOrCustom: String = savedStateHandle.get<String>("workoutIdOrCustom") ?: "custom"
    private val isCustom = workoutIdOrCustom == "custom"

    // Mutable state for the active workout
    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState(
            isCustomWorkout = isCustom,
            isLoading = true
        )
    )

    // Expose UI state as StateFlow
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    // Timer state
    private var timerJob: kotlinx.coroutines.Job? = null

    // Track workout start time
    private var workoutStartTime: Date = Date()

    init {
        loadWorkout()
        workoutStartTime = Date() // Record start time
    }

    private fun loadWorkout() {
        viewModelScope.launch {
            try {
                if (isCustom) {
                    // For custom workout, start with empty exercise list
                    val allExercises = workoutRepository.getAllExercises().first()
                    _uiState.update { it.copy(
                        isLoading = false,
                        isCustomWorkout = true,
                        currentExercises = emptyList(),
                        currentSets = emptyMap(),
                        allExercises = allExercises
                    )}
                } else {
                    // For routine-based workout, load the routine
                    val routineId = workoutIdOrCustom.toIntOrNull() ?: -1
                    workoutRepository.getWorkoutRoutineById(routineId).collect { routine ->
                        if (routine != null) {
                            // Convert routine exercises to performed exercises
                            val performedExercises = routine.exercises.map { exercise ->
                                PerformedExercise(
                                    id = UUID.randomUUID().toString(),
                                    exerciseId = exercise.id,
                                    exerciseName = exercise.name,
                                    targetSets = exercise.sets,
                                    targetReps = exercise.reps,
                                    targetWeight = exercise.weight,
                                    workoutLogId = "" // This will be set when the log is saved
                                )
                            }
                            val sets = performedExercises.associate { it.id to emptyList<PerformedSet>() }
                            val allExercises = workoutRepository.getAllExercises().first()
                            _uiState.update { it.copy(
                                isLoading = false,
                                workoutRoutine = routine,
                                isCustomWorkout = false,
                                currentExercises = performedExercises,
                                currentSets = sets,
                                allExercises = allExercises
                            )}
                        } else {
                            _uiState.update { it.copy(
                                isLoading = false,
                                errorMessage = "Routine not found"
                            )}
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load workout: ${e.message}"
                )}
            }
        }
    }

    // Add a new exercise to custom workout
    fun addExercise(exercise: Exercise) {
        val performedExercise = PerformedExercise(
            id = UUID.randomUUID().toString(),
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            targetSets = exercise.sets,
            targetReps = exercise.reps,
            targetWeight = exercise.weight,
            workoutLogId = "" // This will be set when the log is saved
        )

        _uiState.update { currentState ->
            val newSets = currentState.currentSets.toMutableMap()
            newSets[performedExercise.id] = emptyList()
            currentState.copy(
                currentExercises = currentState.currentExercises + performedExercise,
                currentSets = newSets
            )
        }
    }

    // Add a set to an exercise
    fun addSet(exerciseId: String) {
        val currentSets = _uiState.value.currentSets.toMutableMap()
        val setsForExercise = currentSets[exerciseId]?.toMutableList()
        if (setsForExercise != null) {
            val exercise = _uiState.value.currentExercises.find { it.id == exerciseId }
            val lastSet = setsForExercise.lastOrNull()
            val newSet = PerformedSet(
                id = UUID.randomUUID().toString(),
                reps = lastSet?.reps ?: exercise?.targetReps ?: 0,
                weight = lastSet?.weight ?: exercise?.targetWeight ?: 0.0,
                performedExerciseId = exerciseId
            )
            setsForExercise.add(newSet)
            currentSets[exerciseId] = setsForExercise
            _uiState.update { it.copy(currentSets = currentSets) }
        }
    }

    // Update a set
    fun updateSet(exerciseId: String, setIndex: Int, reps: String, weight: String) {
        val currentSets = _uiState.value.currentSets.toMutableMap()
        val setsForExercise = currentSets[exerciseId]?.toMutableList()
        if (setsForExercise != null && setIndex in setsForExercise.indices) {
            val set = setsForExercise[setIndex]
            val updatedSet = set.copy(
                reps = reps.toIntOrNull() ?: set.reps,
                weight = weight.toDoubleOrNull() ?: set.weight
            )
            setsForExercise[setIndex] = updatedSet
            currentSets[exerciseId] = setsForExercise
            _uiState.update { it.copy(currentSets = currentSets) }
        }
    }

    // Remove a set
    fun removeSet(exerciseId: String, setIndex: Int) {
        val currentSets = _uiState.value.currentSets.toMutableMap()
        val setsForExercise = currentSets[exerciseId]?.toMutableList()
        if (setsForExercise != null && setIndex in setsForExercise.indices) {
            setsForExercise.removeAt(setIndex)
            currentSets[exerciseId] = setsForExercise
            _uiState.update { it.copy(currentSets = currentSets) }
        }
    }

    fun toggleSetCompletion(exerciseId: String, setIndex: Int) {
        val currentSets = _uiState.value.currentSets.toMutableMap()
        val setsForExercise = currentSets[exerciseId]?.toMutableList()
        if (setsForExercise != null && setIndex in setsForExercise.indices) {
            val set = setsForExercise[setIndex]
            val updatedSet = set.copy(isCompleted = !set.isCompleted)
            setsForExercise[setIndex] = updatedSet
            currentSets[exerciseId] = setsForExercise
            _uiState.update { it.copy(currentSets = currentSets) }

            if (updatedSet.isCompleted) {
                // start timer
                startTimer()
            }
        }
    }

    // Start/stop rest timer
    fun toggleTimer() {
        if (_uiState.value.timerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerRunning = true, timerSeconds = 60) } // Default 60 seconds

        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0 && _uiState.value.timerRunning) {
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }

            if (_uiState.value.timerSeconds <= 0) {
                _uiState.update { it.copy(timerRunning = false) }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerRunning = false) }
    }

    fun addTimeToTimer(seconds: Int) {
        _uiState.update { it.copy(timerSeconds = it.timerSeconds + seconds) }
        startTimer()

    }

    // Reset timer to a specific value
    fun resetTimer(seconds: Int) {
        stopTimer()
        _uiState.update { it.copy(timerSeconds = seconds) }
    }

    // Complete workout and save to log
    fun completeWorkout(notes: String = "") {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val endTime = Date()
                val durationMillis = endTime.time - workoutStartTime.time
                val logId = UUID.randomUUID().toString()

                val performedExercisesWithSets = currentState.currentExercises.map { pe ->
                    PerformedExerciseWithSets(
                        performedExercise = pe.copy(workoutLogId = logId),
                        sets = currentState.currentSets[pe.id] ?: emptyList()
                    )
                }

                // Create log entry
                val logEntry = WorkoutLogEntryWithExercises(
                    logEntry = WorkoutLogEntry(
                        id = logId,
                        routineId = currentState.workoutRoutine?.routine?.id,
                        workoutName = currentState.workoutRoutine?.routine?.name
                            ?: "Treino Personalizado",
                        startTime = workoutStartTime,
                        endTime = endTime,
                        durationMillis = durationMillis,
                        notes = notes,
                        caloriesBurned = currentState.workoutRoutine?.routine?.caloriesBurned
                    ),
                    performedExercises = performedExercisesWithSets
                )

                // Save to repository
                workoutRepository.saveWorkoutLog(logEntry)

                // Navigation to log screen happens in UI
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to save workout: ${e.message}") }
            }
        }
    }
}

// Factory for creating ActiveWorkoutViewModel
class ActiveWorkoutViewModelFactory(
    private val savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActiveWorkoutViewModel::class.java)) {
            return ActiveWorkoutViewModel(savedStateHandle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
