package com.example.gymlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gymlog.data.repositories.MockWorkoutRepository
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.models.mockWorkoutRoutines
import com.example.gymlog.ui.components.BottomNavigationBar
import com.example.gymlog.ui.viewmodel.WorkoutDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int
) {
    // Create ViewModel directly with repository
    val detailViewModel: WorkoutDetailViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = androidx.lifecycle.SavedStateHandle(mapOf("workoutId" to workoutId))
                return WorkoutDetailViewModel(savedStateHandle, MockWorkoutRepository()) as T
            }
        }
    )

    // Collect state from ViewModel
    val uiState by detailViewModel.uiState.collectAsState()

    var isPlaying by remember { mutableStateOf(false) } // Local state for media player UI
    var showMenu by remember { mutableStateOf(false) }

    // Use the routine from the UI state
    val workoutRoutine = uiState.routine

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workoutRoutine?.name ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    if (workoutRoutine != null) {
                        // Favorite Button - Use ViewModel state and action
                        IconButton(onClick = { detailViewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (uiState.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                    // More Options Menu
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Favoritos") }, onClick = { navController.navigate("favorites"); showMenu = false }, leadingIcon = { Icon(Icons.Default.Favorite, null) })
                        DropdownMenuItem(text = { Text("Configurações") }, onClick = { navController.navigate("settings"); showMenu = false }, leadingIcon = { Icon(Icons.Default.Settings, null) })
                        DropdownMenuItem(text = { Text("Ajuda") }, onClick = { navController.navigate("help"); showMenu = false }, leadingIcon = { Icon(Icons.Default.Help, null) })
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Handle Loading, Error, and Success states from ViewModel
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Erro: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                }
            }
            workoutRoutine != null -> {
                // Display details when routine is available
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Workout Image Header
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                        Image(
                            painter = painterResource(id = workoutRoutine.image),
                            contentDescription = workoutRoutine.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                            Text(workoutRoutine.name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FitnessCenter, "Categoria", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(workoutRoutine.category, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(Icons.Default.Speed, "Dificuldade", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(workoutRoutine.difficulty, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            }
                        }
                        if (workoutRoutine.videoUrl != null || workoutRoutine.audioUrl != null) {
                            FloatingActionButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier.align(Alignment.Center).size(56.dp),
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    if (isPlaying) "Pausar" else "Reproduzir",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    // Detailed Information Section
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatItem(icon = Icons.Default.Timer, value = "${workoutRoutine.duration}", label = "minutos")
                            StatItem(icon = Icons.Default.LocalFireDepartment, value = "${workoutRoutine.caloriesBurned}", label = "calorias")
                            StatItem(icon = Icons.Default.FitnessCenter, value = "${workoutRoutine.exercises.size}", label = "exercícios")
                        }
                        Divider()
                        Text("Descrição", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        Text(workoutRoutine.description, style = MaterialTheme.typography.bodyMedium)

                        // Media Player Card (Simplified - Needs proper implementation)
                        if (workoutRoutine.videoUrl != null || workoutRoutine.audioUrl != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                // ... (Media player UI - keep as is for now)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Exercícios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        workoutRoutine.exercises.forEachIndexed { index, exercise ->
                            ExerciseItem(exercise = exercise, index = index + 1)
                            if (index < workoutRoutine.exercises.size - 1) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Rotinas Relacionadas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            val relatedRoutines = mockWorkoutRoutines.filter {
                                it.category == workoutRoutine.category && it.id != workoutRoutine.id
                            }.take(5)
                            items(relatedRoutines, key = { it.id }) { relatedWorkout ->
                                RelatedWorkoutCard(workout = relatedWorkout) {
                                    navController.navigate("workout_details/${relatedWorkout.id}")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            // Optional: Handle case where routine is null after loading (e.g., invalid ID)
            workoutRoutine == null && !uiState.isLoading && uiState.errorMessage == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Rotina não encontrada.")
                }
            }
        }
    }
}

// StatItem, ExerciseItem, RelatedWorkoutCard remain the same

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ExerciseItem(exercise: Exercise, index: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("$index.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
        Image(painterResource(id = exercise.exercisePicture), exercise.name, Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, style = MaterialTheme.typography.titleMedium)
            Text("${exercise.sets} séries x ${exercise.reps} reps", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedWorkoutCard(workout: WorkoutRoutine, onClick: () -> Unit) {
    Card(modifier = Modifier.width(160.dp).clickable(onClick = onClick), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            Image(painterResource(id = workout.image), workout.name, Modifier.fillMaxWidth().height(90.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(workout.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${workout.duration} min", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
