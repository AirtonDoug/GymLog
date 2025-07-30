package com.example.gymlog.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymlog.data.database.dao.ExerciseDao
import com.example.gymlog.data.database.dao.FaqDao
import com.example.gymlog.data.database.dao.ProfileDao
import com.example.gymlog.data.database.dao.WorkoutLogDao
import com.example.gymlog.data.database.dao.WorkoutRoutineDao
import com.example.gymlog.models.Converters
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.FAQ
import com.example.gymlog.models.PerformedExercise
import com.example.gymlog.models.PerformedSet
import com.example.gymlog.models.ProfileData
import com.example.gymlog.models.WorkoutLogEntry
import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.models.WorkoutRoutineExerciseCrossRef
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymlog.models.faqList
import com.example.gymlog.models.mockWorkoutRoutines
import com.example.gymlog.models.mockWorkoutRoutinesWithExercises
import com.example.gymlog.models.profileData
import com.example.gymlog.models.exerciseList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProfileData::class,
        Exercise::class,
        WorkoutRoutine::class,
        WorkoutRoutineExerciseCrossRef::class,
        FAQ::class,
        PerformedSet::class,
        PerformedExercise::class,
        WorkoutLogEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymLogDatabase : RoomDatabase() {

    abstract fun workoutRoutineDao(): WorkoutRoutineDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun profileDao(): ProfileDao
    abstract fun faqDao(): FaqDao

    companion object {
        @Volatile
        private var INSTANCE: GymLogDatabase? = null

        fun getDatabase(context: Context): GymLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymLogDatabase::class.java,
                    "gym_log_database"
                )
                .addCallback(PrepopulateCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PrepopulateCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Pre-populate data here
                    database.profileDao().insertProfile(profileData)
                    database.exerciseDao().insertExercises(exerciseList)
                    database.workoutRoutineDao().insertRoutines(mockWorkoutRoutines)
                    val crossRefs = mockWorkoutRoutinesWithExercises.flatMap { routineWithExercises ->
                        routineWithExercises.exercises.map { exercise ->
                            WorkoutRoutineExerciseCrossRef(
                                routineId = routineWithExercises.routine.id,
                                exerciseId = exercise.id
                            )
                        }
                    }
                    database.workoutRoutineDao().insertCrossRefs(crossRefs)
                    database.faqDao().insertFaqs(faqList)
                }
            }
        }
    }
}
