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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gymlog.models.Exercise
import com.example.gymlog.models.WorkoutRoutine // Corrected import
import com.example.gymlog.models.mockWorkoutRoutines // Corrected import
import com.example.gymlog.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int,
    onToggleFavorite: (WorkoutRoutine) -> Unit // Corrected type
) {
    // Find the routine by ID
    val workout = mockWorkoutRoutines.find { it.id == workoutId }
        ?: return // If not found, don't display anything

    var isFavorite by remember { mutableStateOf(workout.isFavorite) }
    var isPlaying by remember { mutableStateOf(false) } // State for media playback
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    // Favorite Button
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            // Create a new object for state update if needed
                            onToggleFavorite(workout.copy(isFavorite = isFavorite))
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Workout Image Header
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
                Box( // Dark overlay for text contrast
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(workout.name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, "Categoria", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(workout.category, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.Speed, "Dificuldade", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(workout.difficulty, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }
                // Play Button (if media exists)
                if (workout.videoUrl != null || workout.audioUrl != null) {
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
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(icon = Icons.Default.Timer, value = "${workout.duration}", label = "minutos")
                    StatItem(icon = Icons.Default.LocalFireDepartment, value = "${workout.caloriesBurned}", label = "calorias")
                    StatItem(icon = Icons.Default.FitnessCenter, value = "${workout.exercises.size}", label = "exercícios")
                }
                Divider()

                // Description
                Text("Descrição", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                Text(workout.description, style = MaterialTheme.typography.bodyMedium)

                // Media Player Card (Simplified)
                if (workout.videoUrl != null || workout.audioUrl != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(if (workout.videoUrl != null) "Vídeo do treino" else "Áudio do treino", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { /* TODO: Implement seek back */ }) { Icon(Icons.Default.SkipPrevious, "Retroceder") }
                                IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary, CircleShape)) {
                                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (isPlaying) "Pausar" else "Reproduzir", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                IconButton(onClick = { /* TODO: Implement seek forward */ }) { Icon(Icons.Default.SkipNext, "Avançar") }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(progress = if (isPlaying) 0.3f else 0f, modifier = Modifier.fillMaxWidth()) // TODO: Link progress
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isPlaying) "1:30" else "0:00", style = MaterialTheme.typography.bodySmall) // TODO: Link time
                                Text("5:00", style = MaterialTheme.typography.bodySmall) // TODO: Link total duration
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Exercises List
                Text("Exercícios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                workout.exercises.forEachIndexed { index, exercise ->
                    ExerciseItem(exercise = exercise, index = index + 1)
                    if (index < workout.exercises.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Related Workouts (LazyRow)
                Text("Rotinas Relacionadas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Filter related routines (example: same category, different ID)
                    val relatedRoutines = mockWorkoutRoutines.filter {
                        it.category == workout.category && it.id != workout.id
                    }.take(5) // Limit number of related items

                    items(relatedRoutines, key = { it.id }) { relatedWorkout ->
                        RelatedWorkoutCard(workout = relatedWorkout) {
                            navController.navigate("workout_details/${relatedWorkout.id}") {
                                // Avoid multiple copies of the same screen on the back stack
                                popUpTo("workout_details/${workout.id}") { inclusive = true }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

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
        Text(
            text = "$index.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(30.dp)
        )
        Image(
            painter = painterResource(id = exercise.exercisePicture),
            contentDescription = exercise.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, style = MaterialTheme.typography.titleMedium)
            Text("${exercise.sets} séries x ${exercise.reps} reps", style = MaterialTheme.typography.bodyMedium)
            // Optionally show weight: Text("${exercise.weight} kg", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedWorkoutCard(workout: WorkoutRoutine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp) // Fixed width for horizontal scroll
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = workout.image),
                contentDescription = workout.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(workout.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${workout.duration} min", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
