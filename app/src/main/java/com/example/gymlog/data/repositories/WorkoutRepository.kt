package com.example.gymlog.data.repositories

import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.models.mockWorkoutRoutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Interface for accessing workout routine data.
 */
interface WorkoutRepository {
    fun getWorkoutRoutines(): Flow<List<WorkoutRoutine>>
    fun getWorkoutRoutineById(id: Int): Flow<WorkoutRoutine?>
    fun getFavoriteWorkoutRoutines(): Flow<List<WorkoutRoutine>>
    suspend fun toggleFavorite(routineId: Int)
    suspend fun clearFavorites()
}

/**
 * Mock implementation of WorkoutRepository using in-memory data.
 */
class MockWorkoutRepository : WorkoutRepository {

    // Use MutableStateFlow to hold the state of routines, including favorites
    private val _routines = MutableStateFlow(mockWorkoutRoutines)

    // Keep track of favorite IDs separately for easier toggling
    private val _favoriteIds = MutableStateFlow(
        mockWorkoutRoutines.filter { it.isFavorite }.map { it.id }.toSet()
    )

    override fun getWorkoutRoutines(): Flow<List<WorkoutRoutine>> {
        // Combine routine data with favorite status
        return _routines.combine(_favoriteIds) { routines, favIds ->
            routines.map { it.copy(isFavorite = it.id in favIds) }
        }
    }

    override fun getWorkoutRoutineById(id: Int): Flow<WorkoutRoutine?> {
        return getWorkoutRoutines().map { routines -> routines.find { it.id == id } }
    }

    override fun getFavoriteWorkoutRoutines(): Flow<List<WorkoutRoutine>> {
        return getWorkoutRoutines().map { routines -> routines.filter { it.isFavorite } }
    }

    override suspend fun toggleFavorite(routineId: Int) {
        _favoriteIds.update { currentIds ->
            if (routineId in currentIds) {
                currentIds - routineId
            } else {
                currentIds + routineId
            }
        }
        // Note: We are not modifying the original mockWorkoutRoutines list directly.
        // The isFavorite status is derived dynamically in the flows.
    }

    override suspend fun clearFavorites() {
        _favoriteIds.value = emptySet()
    }
}

// Helper function for combining flows (add if not available)
fun <T1, T2, R> Flow<T1>.combine(flow: Flow<T2>, transform: suspend (T1, T2) -> R): Flow<R> {
    return kotlinx.coroutines.flow.combine(this, flow, transform)
}
