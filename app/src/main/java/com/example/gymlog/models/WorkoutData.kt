package com.example.gymlog.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.gymlog.R
import java.util.Date
import java.util.UUID

// --- Type Converters ---

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// --- Entities ---

@Entity(tableName = "profile")
data class ProfileData(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Double,
    val weight: Double,
    val profilePicture: Int
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val exercisePicture: Int
)

@Entity(tableName = "workout_routines")
data class WorkoutRoutine(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val duration: Int,
    val difficulty: String,
    val category: String,
    val image: Int,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    var isFavorite: Boolean = false,
    val rating: Float = 0f,
    val caloriesBurned: Int = 0
)

@Entity(tableName = "workout_routine_exercise_cross_ref", primaryKeys = ["routineId", "exerciseId"])
data class WorkoutRoutineExerciseCrossRef(
    val routineId: Int,
    val exerciseId: Int
)

data class WorkoutRoutineWithExercises(
    @Embedded val routine: WorkoutRoutine,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutRoutineExerciseCrossRef::class,
            parentColumn = "routineId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<Exercise>
)

@Entity(tableName = "faqs")
data class FAQ(
    @PrimaryKey val id: Int,
    val question: String,
    val answer: String
)

@Entity(
    tableName = "performed_sets",
    foreignKeys = [
        ForeignKey(
            entity = PerformedExercise::class,
            parentColumns = ["id"],
            childColumns = ["performedExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PerformedSet(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val performedExerciseId: String,
    var reps: Int,
    var weight: Double,
    var isCompleted: Boolean = false
)

@Entity(
    tableName = "performed_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutLogEntry::class,
            parentColumns = ["id"],
            childColumns = ["workoutLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PerformedExercise(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workoutLogId: String,
    val exerciseId: Int,
    val exerciseName: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Double
)

data class PerformedExerciseWithSets(
    @Embedded val performedExercise: PerformedExercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "performedExerciseId"
    )
    val sets: List<PerformedSet>
)


@Entity(tableName = "workout_log_entries")
@TypeConverters(Converters::class)
data class WorkoutLogEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val routineId: Int? = null,
    val workoutName: String,
    val startTime: Date,
    var endTime: Date? = null,
    var durationMillis: Long = 0,
    var notes: String? = null,
    var caloriesBurned: Int? = null
)

data class WorkoutLogEntryWithExercises(
    @Embedded val logEntry: WorkoutLogEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutLogId",
        entity = PerformedExercise::class
    )
    val performedExercises: List<PerformedExerciseWithSets>
)


// --- Mock Data ---

val profileData = ProfileData(
    id = 1,
    name = "Julia Oliveira",
    height = 170.0,
    weight = 75.0,
    profilePicture = R.drawable.profile
)

val exerciseList = listOf(
    Exercise(
        id = 1,
        name = "Supino Reto",
        description = "Exercício de supino reto com barra.",
        sets = 3,
        reps = 12,
        weight = 100.0,
        exercisePicture = R.drawable.supino
    ),
    Exercise(
        id = 2,
        name = "Rosca Direta",
        description = "Exercício de rosca direta com barra.",
        sets = 3,
        reps = 12,
        weight = 20.0,
        exercisePicture = R.drawable.rosca_direta
    ),
    Exercise(
        id = 3,
        name = "Agachamento",
        description = "Exercício de agachamento com barra.",
        sets = 3,
        reps = 12,
        weight = 50.0,
        exercisePicture = R.drawable.agachamento
    ),
    Exercise(
        id = 4,
        name = "Barra fixa",
        description = "Exercício de costas com barra fixa.",
        sets = 3,
        reps = 12,
        weight = profileData.weight,
        exercisePicture = R.drawable.supino
    ),
    Exercise(
        id = 5,
        name = "Levantamento terra",
        description = "Exercício de levantamento terra com barra.",
        sets = 3,
        reps = 12,
        weight = 100.0,
        exercisePicture = R.drawable.deadlift
    ),
    Exercise(
        id = 6,
        name = "Tríceps Corda",
        description = "Exercício de tríceps com corda na polia.",
        sets = 3,
        reps = 15,
        weight = 25.0,
        exercisePicture = R.drawable.rosca_direta
    ),
    Exercise(
        id = 7,
        name = "Rosca Martelo",
        description = "Exercício de bíceps com halteres.",
        sets = 3,
        reps = 12,
        weight = 15.0,
        exercisePicture = R.drawable.rosca_direta
    ),
    Exercise(
        id = 8,
        name = "Leg Press",
        description = "Exercício de pernas no aparelho leg press.",
        sets = 4,
        reps = 10,
        weight = 200.0,
        exercisePicture = R.drawable.agachamento
    ),
    Exercise(
        id = 9,
        name = "Remada Curvada",
        description = "Exercício de costas com barra.",
        sets = 3,
        reps = 12,
        weight = 60.0,
        exercisePicture = R.drawable.supino
    ),
    Exercise(
        id = 10,
        name = "Puxada Frontal",
        description = "Exercício de costas na polia alta.",
        sets = 3,
        reps = 12,
        weight = 70.0,
        exercisePicture = R.drawable.supino
    ),
    Exercise(
        id = 11,
        name = "Burpees",
        description = "Exercício funcional de corpo inteiro.",
        sets = 5,
        reps = 20,
        weight = 0.0,
        exercisePicture = R.drawable.deadlift
    ),
    Exercise(
        id = 12,
        name = "Mountain Climbers",
        description = "Exercício cardiovascular.",
        sets = 5,
        reps = 30,
        weight = 0.0,
        exercisePicture = R.drawable.deadlift
    )
)

val mockWorkoutRoutines = listOf(
    WorkoutRoutine(
        id = 1,
        name = "Treino Full Body",
        description = "Um treino completo que trabalha todos os principais grupos musculares em uma única sessão. Ideal para quem tem pouco tempo disponível e quer maximizar resultados.",
        duration = 60,
        difficulty = "Intermediário",
        category = "Força",
        image = R.drawable.supino,
        videoUrl = "https://example.com/videos/fullbody.mp4",
        caloriesBurned = 450
    ),
    WorkoutRoutine(
        id = 2,
        name = "Treino de Braços",
        description = "Foco intenso em bíceps, tríceps e antebraços para desenvolver força e definição nos membros superiores.",
        duration = 45,
        difficulty = "Iniciante",
        category = "Força",
        image = R.drawable.rosca_direta,
        audioUrl = "https://example.com/audio/arms_guidance.mp3",
        caloriesBurned = 300
    ),
    WorkoutRoutine(
        id = 3,
        name = "Treino de Pernas",
        description = "Treino focado em quadríceps, posteriores, glúteos e panturrilhas para desenvolver força e potência nos membros inferiores.",
        duration = 50,
        difficulty = "Avançado",
        category = "Força",
        image = R.drawable.agachamento,
        videoUrl = "https://example.com/videos/legs.mp4",
        isFavorite = true,
        caloriesBurned = 500
    ),
    WorkoutRoutine(
        id = 4,
        name = "Treino de Costas",
        description = "Foco em desenvolver os músculos das costas, incluindo latíssimo do dorso, trapézio e romboides.",
        duration = 40,
        difficulty = "Intermediário",
        category = "Força",
        image = R.drawable.supino,
        caloriesBurned = 380
    ),
    WorkoutRoutine(
        id = 5,
        name = "Treino HIIT",
        description = "Treino intervalado de alta intensidade para queima de gordura e condicionamento cardiovascular.",
        duration = 30,
        difficulty = "Avançado",
        category = "Cardio",
        image = R.drawable.deadlift,
        videoUrl = "https://example.com/videos/hiit.mp4",
        isFavorite = true,
        caloriesBurned = 400
    )
)

val mockWorkoutRoutinesWithExercises = listOf(
    WorkoutRoutineWithExercises(
        routine = mockWorkoutRoutines[0],
        exercises = listOf(exerciseList[0], exerciseList[2], exerciseList[3], exerciseList[4])
    ),
    WorkoutRoutineWithExercises(
        routine = mockWorkoutRoutines[1],
        exercises = listOf(exerciseList[1], exerciseList[5], exerciseList[6])
    ),
    WorkoutRoutineWithExercises(
        routine = mockWorkoutRoutines[2],
        exercises = listOf(exerciseList[2], exerciseList[4], exerciseList[7])
    ),
    WorkoutRoutineWithExercises(
        routine = mockWorkoutRoutines[3],
        exercises = listOf(exerciseList[3], exerciseList[8], exerciseList[9])
    ),
    WorkoutRoutineWithExercises(
        routine = mockWorkoutRoutines[4],
        exercises = listOf(exerciseList[10], exerciseList[11])
    )
)


val faqList = listOf(
    FAQ(1, "Como registrar um novo treino?", "Na tela 'Log', clique no botão '+' e selecione uma rotina ou crie um treino personalizado."),
    FAQ(2, "Como marcar uma rotina como favorita?", "Na tela de detalhes da rotina (acessível pela tela inicial), clique no ícone de estrela."),
    FAQ(3, "Como usar o timer de descanso?", "Durante o registro de um treino, após completar uma série, clique no ícone de cronômetro para iniciar o descanso."),
    FAQ(4, "Como editar ou excluir um treino do histórico?", "Na tela 'Log', encontre o registro desejado e use os ícones de lápis (editar) ou lixeira (excluir)."),
    FAQ(5, "Como criar uma rotina personalizada?", "Atualmente, você pode registrar um treino personalizado na hora. A funcionalidade de salvar rotinas personalizadas será adicionada em breve.")
)
