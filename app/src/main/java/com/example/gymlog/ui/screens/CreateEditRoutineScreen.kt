package com.example.gymlog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gymlog.models.Exercise
import com.example.gymlog.ui.viewmodel.CreateEditRoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditRoutineScreen(
    navController: NavController,
    createEditRoutineViewModel: CreateEditRoutineViewModel
) {
    val uiState by createEditRoutineViewModel.uiState.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.routine.id == 0) "Create Routine" else "Edit Routine") },
                actions = {
                    Button(onClick = { createEditRoutineViewModel.saveRoutine() }) {
                        Text("Save")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExerciseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add exercise")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.routine.name,
                onValueChange = { createEditRoutineViewModel.onRoutineNameChange(it) },
                label = { Text("Routine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = uiState.routine.description,
                onValueChange = { createEditRoutineViewModel.onRoutineDescriptionChange(it) },
                label = { Text("Routine Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Exercises:", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        onRemove = { createEditRoutineViewModel.removeExercise(exercise) }
                    )
                }
            }
        }
    }

    if (showAddExerciseDialog) {
        AddExerciseDialog(
            allExercises = uiState.allExercises,
            onAddExercise = { createEditRoutineViewModel.addExercise(it) },
            onDismiss = { showAddExerciseDialog = false }
        )
    }
}

@Composable
fun ExerciseItem(exercise: Exercise, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = exercise.name)
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove exercise")
        }
    }
}

@Composable
fun AddExerciseDialog(
    allExercises: List<Exercise>,
    onAddExercise: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercise") },
        text = {
            LazyColumn {
                items(allExercises) { exercise ->
                    TextButton(onClick = { onAddExercise(exercise); onDismiss() }) {
                        Text(exercise.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
