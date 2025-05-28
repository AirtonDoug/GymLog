package com.example.gymlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.WorkoutItem
import com.example.gymlog.models.mockWorkouts
import com.example.gymlog.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int,
    onToggleFavorite: (WorkoutItem) -> Unit
) {
    // Encontrar o treino pelo ID
    val workout = mockWorkouts.find { it.id == workoutId }
        ?: return // Se não encontrar, não exibe nada

    var isFavorite by remember { mutableStateOf(workout.isFavorite) }
    var isPlaying by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            onToggleFavorite(workout.copy(isFavorite = isFavorite))
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Favoritos") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favoritos"
                                )
                            },
                            onClick = {
                                navController.navigate("favorites")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Configurações"
                                )
                            },
                            onClick = {
                                navController.navigate("settings")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ajuda") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Help,
                                    contentDescription = "Ajuda"
                                )
                            },
                            onClick = {
                                navController.navigate("help")
                                showMenu = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagem do treino
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = workout.image),
                    contentDescription = workout.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay para informações sobre a imagem
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )

                // Informações básicas
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Categoria",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = workout.category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Dificuldade",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = workout.difficulty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                // Botão de play para vídeo ou áudio
                if (workout.videoUrl != null || workout.audioUrl != null) {
                    FloatingActionButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(56.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Informações detalhadas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Estatísticas do treino
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Timer,
                        value = "${workout.duration}",
                        label = "minutos"
                    )

                    StatItem(
                        icon = Icons.Default.LocalFireDepartment,
                        value = "${workout.caloriesBurned}",
                        label = "calorias"
                    )

                    StatItem(
                        icon = Icons.Default.FitnessCenter,
                        value = "${workout.exercises.size}",
                        label = "exercícios"
                    )
                }

                Divider()

                // Descrição
                Text(
                    text = "Descrição",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de multimídia
                if (workout.videoUrl != null || workout.audioUrl != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = if (workout.videoUrl != null) "Vídeo do treino" else "Áudio do treino",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { /* Retroceder */ }) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Retroceder"
                                    )
                                }

                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                IconButton(onClick = { /* Avançar */ }) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Avançar"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Barra de progresso
                            LinearProgressIndicator(
                                progress = if (isPlaying) 0.3f else 0f,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Tempo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isPlaying) "1:30" else "0:00",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "5:00",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de exercícios
                Text(
                    text = "Exercícios",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                workout.exercises.forEachIndexed { index, exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        index = index + 1
                    )

                    if (index < workout.exercises.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Treinos relacionados
                Text(
                    text = "Treinos relacionados",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Filtrar treinos relacionados (mesma categoria, excluindo o atual)
                val relatedWorkouts = mockWorkouts.filter {
                    it.category == workout.category && it.id != workout.id
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(relatedWorkouts) { relatedWorkout ->
                        RelatedWorkoutItem(
                            workout = relatedWorkout,
                            onClick = {
                                navController.navigate("workout_details/${relatedWorkout.id}")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* Iniciar treino */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Treino")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = { /* Compartilhar */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartilhar")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    index: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número do exercício
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Informações do exercício
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${exercise.sets} séries",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${exercise.reps} repetições",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${exercise.weight} kg",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Imagem do exercício
        Image(
            painter = painterResource(id = exercise.exercisePicture),
            contentDescription = exercise.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun RelatedWorkoutItem(
    workout: WorkoutItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = workout.image),
                contentDescription = workout.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Duração",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.duration} min",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
