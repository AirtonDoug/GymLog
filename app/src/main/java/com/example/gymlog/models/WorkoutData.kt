package com.example.gymlog.models

import com.example.gymlog.R

/**
 * Modelo de dados para os treinos
 */
data class WorkoutItem(
    val id: Int,
    val name: String,
    val description: String,
    val duration: Int, // em minutos
    val difficulty: String, // "Iniciante", "Intermediário", "Avançado"
    val category: String, // "Força", "Cardio", "Flexibilidade", etc.
    val image: Int,
    val exercises: List<Exercise>,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    val isFavorite: Boolean = false,
    val rating: Float = 0f,
    val caloriesBurned: Int = 0
)

/**
 * Lista mockada de treinos para a aplicação
 */
val mockWorkouts = listOf(
    WorkoutItem(
        id = 1,
        name = "Treino Full Body",
        description = "Um treino completo que trabalha todos os principais grupos musculares em uma única sessão. Ideal para quem tem pouco tempo disponível e quer maximizar resultados.",
        duration = 60,
        difficulty = "Intermediário",
        category = "Força",
        image = R.drawable.supino,
        exercises = listOf(
            exerciseList[0], // Supino Reto
            exerciseList[2], // Agachamento
            exerciseList[3], // Barra fixa
            exerciseList[4]  // Levantamento terra
        ),
        videoUrl = "https://example.com/videos/fullbody.mp4",
        caloriesBurned = 450
    ),
    WorkoutItem(
        id = 2,
        name = "Treino de Braços",
        description = "Foco intenso em bíceps, tríceps e antebraços para desenvolver força e definição nos membros superiores.",
        duration = 45,
        difficulty = "Iniciante",
        category = "Força",
        image = R.drawable.rosca_direta,
        exercises = listOf(
            exerciseList[1], // Rosca Direta
            Exercise(
                name = "Tríceps Corda",
                description = "Exercício de tríceps com corda na polia.",
                sets = 3,
                reps = 15,
                weight = 25.0,
                exercisePicture = R.drawable.rosca_direta
            ),
            Exercise(
                name = "Rosca Martelo",
                description = "Exercício de bíceps com halteres.",
                sets = 3,
                reps = 12,
                weight = 15.0,
                exercisePicture = R.drawable.rosca_direta
            )
        ),
        audioUrl = "https://example.com/audio/arms_guidance.mp3",
        caloriesBurned = 300
    ),
    WorkoutItem(
        id = 3,
        name = "Treino de Pernas",
        description = "Treino focado em quadríceps, posteriores, glúteos e panturrilhas para desenvolver força e potência nos membros inferiores.",
        duration = 50,
        difficulty = "Avançado",
        category = "Força",
        image = R.drawable.agachamento,
        exercises = listOf(
            exerciseList[2], // Agachamento
            exerciseList[4], // Levantamento terra
            Exercise(
                name = "Leg Press",
                description = "Exercício de pernas no aparelho leg press.",
                sets = 4,
                reps = 10,
                weight = 200.0,
                exercisePicture = R.drawable.agachamento
            )
        ),
        videoUrl = "https://example.com/videos/legs.mp4",
        isFavorite = true,
        caloriesBurned = 500
    ),
    WorkoutItem(
        id = 4,
        name = "Treino de Costas",
        description = "Foco em desenvolver os músculos das costas, incluindo latíssimo do dorso, trapézio e romboides.",
        duration = 40,
        difficulty = "Intermediário",
        category = "Força",
        image = R.drawable.pull_up,
        exercises = listOf(
            exerciseList[3], // Barra fixa
            Exercise(
                name = "Remada Curvada",
                description = "Exercício de costas com barra.",
                sets = 3,
                reps = 12,
                weight = 60.0,
                exercisePicture = R.drawable.pull_up
            ),
            Exercise(
                name = "Puxada Frontal",
                description = "Exercício de costas na polia alta.",
                sets = 3,
                reps = 12,
                weight = 70.0,
                exercisePicture = R.drawable.pull_up
            )
        ),
        caloriesBurned = 380
    ),
    WorkoutItem(
        id = 5,
        name = "Treino HIIT",
        description = "Treino intervalado de alta intensidade para queima de gordura e condicionamento cardiovascular.",
        duration = 30,
        difficulty = "Avançado",
        category = "Cardio",
        image = R.drawable.deadlift,
        exercises = listOf(
            Exercise(
                name = "Burpees",
                description = "Exercício funcional de corpo inteiro.",
                sets = 5,
                reps = 20,
                weight = 0.0,
                exercisePicture = R.drawable.deadlift
            ),
            Exercise(
                name = "Mountain Climbers",
                description = "Exercício cardiovascular.",
                sets = 5,
                reps = 30,
                weight = 0.0,
                exercisePicture = R.drawable.deadlift
            )
        ),
        videoUrl = "https://example.com/videos/hiit.mp4",
        isFavorite = true,
        caloriesBurned = 400
    )
)

/**
 * Perguntas frequentes para a tela de Ajuda
 */
data class FAQ(
    val id: Int,
    val question: String,
    val answer: String
)

val faqList = listOf(
    FAQ(
        id = 1,
        question = "Como adicionar um novo treino?",
        answer = "Na tela inicial, clique no botão '+' no canto inferior direito e preencha os detalhes do seu treino."
    ),
    FAQ(
        id = 2,
        question = "Como marcar um treino como favorito?",
        answer = "Na tela de detalhes do treino, clique no ícone de estrela no canto superior direito."
    ),
    FAQ(
        id = 3,
        question = "Como ativar o modo escuro?",
        answer = "Vá para a tela de Configurações através do menu de três pontinhos e ative a opção 'Modo Escuro'."
    ),
    FAQ(
        id = 4,
        question = "Como filtrar treinos por categoria?",
        answer = "Na tela inicial, use o campo de busca e digite a categoria desejada, como 'Força' ou 'Cardio'."
    ),
    FAQ(
        id = 5,
        question = "Como redefinir minhas preferências?",
        answer = "Na tela de Configurações, clique no botão 'Redefinir Preferências' na parte inferior da tela."
    )
)
