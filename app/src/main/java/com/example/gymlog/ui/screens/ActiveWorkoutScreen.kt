package com.example.gymlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gymlog.models.*
import com.example.gymlog.ui.viewmodel.ActiveWorkoutViewModel
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    activeWorkoutViewModel: ActiveWorkoutViewModel
) {
    val uiState by activeWorkoutViewModel.uiState.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.workoutRoutine?.routine?.name ?: "Treino Personalizado", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Finish Workout Button
                    Button(onClick = { showNotesDialog = true }) {
                        Text("Finalizar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.isCustomWorkout) {
                FloatingActionButton(onClick = { showAddExerciseDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Exercício")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.currentExercises, key = { it.id }) { performedExercise ->
                PerformedExerciseCard(
                    exercise = performedExercise,
                    sets = uiState.currentSets[performedExercise.id] ?: emptyList(),
                    onAddSet = { activeWorkoutViewModel.addSet(performedExercise.id) },
                    onUpdateSet = { setIndex, reps, weight -> activeWorkoutViewModel.updateSet(performedExercise.id, setIndex, reps, weight) },
                    onToggleSetCompletion = { setIndex -> activeWorkoutViewModel.toggleSetCompletion(performedExercise.id, setIndex) },
                    onStartRestTimer = { activeWorkoutViewModel.startTimer() }
                )
            }

            // Button to add exercise in custom mode (alternative to FAB)
            if (uiState.isCustomWorkout) {
                item {
                    OutlinedButton(
                        onClick = { showAddExerciseDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Exercício")
                    }
                }
            }
        }

        // Rest Timer Dialog
        if (uiState.timerRunning) {
            RestTimerDialog(
                totalSeconds = 60, // you can make this configurable
                currentTime = uiState.timerSeconds,
                isRunning = uiState.timerRunning,
                onDismiss = { activeWorkoutViewModel.stopTimer() },
                onStop = { activeWorkoutViewModel.stopTimer() },
                onStart = { activeWorkoutViewModel.startTimer() },
                onReset = { activeWorkoutViewModel.resetTimer(60) },
                onAddTime = { activeWorkoutViewModel.addTimeToTimer(15) },
                onSetTotalTime = { activeWorkoutViewModel.resetTimer(it) }
            )
        }

        // Add Exercise Dialog (for custom workouts)
        if (showAddExerciseDialog) {
            AddExerciseDialog(
                allExercises = uiState.allExercises, // You need to add this to the uiState
                onDismiss = { showAddExerciseDialog = false },
                onExerciseSelected = {
                    activeWorkoutViewModel.addExercise(it)
                    showAddExerciseDialog = false
                }
            )
        }

        if (showNotesDialog) {
            AlertDialog(
                onDismissRequest = { showNotesDialog = false },
                title = { Text("Adicionar anotações") },
                text = {
                    TextField(value = notes, onValueChange = {notes = it}, label = {Text("Anotações")})
                },
                confirmButton = {
                    Button(onClick = {
                        activeWorkoutViewModel.completeWorkout(notes)
                        navController.popBackStack("log", inclusive = false)
                    }) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showNotesDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformedExerciseCard(
    exercise: PerformedExercise,
    sets: List<PerformedSet>,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, String, String) -> Unit,
    onToggleSetCompletion: (Int) -> Unit,
    onStartRestTimer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(exercise.exerciseName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Header Row
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Set", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelMedium)
                Text("Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Peso (kg)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Status", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelMedium)
            }
            Divider()

            // Sets List
            sets.forEachIndexed { setIndex, set ->
                PerformedSetRow(
                    set = set,
                    setNumber = setIndex + 1,
                    onUpdate = { reps, weight -> onUpdateSet(setIndex, reps, weight) },
                    onToggleCompletion = { onToggleSetCompletion(setIndex) }
                )
                Divider()
            }

            // Add Set Button
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = onAddSet) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Set")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar Set")
                }
                IconButton(onClick = onStartRestTimer) {
                    Icon(Icons.Default.Timer, contentDescription = "Iniciar Timer Descanso")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformedSetRow(
    set: PerformedSet,
    setNumber: Int,
    onUpdate: (String, String) -> Unit,
    onToggleCompletion: () -> Unit
) {
    var reps by remember(set.reps) { mutableStateOf(set.reps.toString()) }
    var weight by remember(set.weight) { mutableStateOf(set.weight.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(setNumber.toString(), modifier = Modifier.weight(0.5f))

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it; onUpdate(it, weight) },
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

            )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it; onUpdate(reps, it) },
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),

            )

        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { onToggleCompletion() },
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
fun RestTimerDialog(
    totalSeconds: Int,
    currentTime: Int,
    isRunning: Boolean,
    onDismiss: () -> Unit,
    onStop: () -> Unit,
    onStart: () -> Unit,
    onReset: () -> Unit,
    onAddTime: () -> Unit,
    onSetTotalTime: (Int) -> Unit // Allow changing the default rest time
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Timer de Descanso") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(currentTime),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = if (totalSeconds > 0) currentTime.toFloat() / totalSeconds else 0f,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Quick add time buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onSetTotalTime(30) }, enabled = !isRunning) { Text("30s") }
                    Button(onClick = { onSetTotalTime(60) }, enabled = !isRunning) { Text("60s") }
                    Button(onClick = { onSetTotalTime(90) }, enabled = !isRunning) { Text("90s") }
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = onReset, enabled = !isRunning) {
                    Icon(Icons.Default.Refresh, contentDescription = "Resetar")
                }
                IconButton(onClick = if (isRunning) onStop else onStart) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar"
                    )
                }
                IconButton(onClick = onAddTime, enabled = isRunning) {
                    Icon(Icons.Default.AddAlarm, contentDescription = "Adicionar 15s")
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar")
                }
            }
        }
    )
}

@Composable
fun AddExerciseDialog(
    allExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredExercises = remember(searchQuery) {
        allExercises.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Exercício") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar exercício...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(filteredExercises, key = { it.id }) { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onExerciseSelected(exercise) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Optional: Add exercise image thumbnail
                            Text(exercise.name, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        }
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Helper function to format time
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
