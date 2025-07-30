package com.example.gymlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gymlog.models.WorkoutRoutineWithExercises
import com.example.gymlog.ui.viewmodel.MyRoutinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutinesScreen(
    navController: NavController,
    myRoutinesViewModel: MyRoutinesViewModel
) {
    val uiState by myRoutinesViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Routines") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_routine") }) {
                Icon(Icons.Default.Add, contentDescription = "Create new routine")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.routines, key = { it.routine.id }) { routine ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                myRoutinesViewModel.deleteRoutine(routine.routine.id)
                                true // Confirm the dismiss
                            }
                            SwipeToDismissBoxValue.StartToEnd -> {
                                navController.navigate("create_edit_routine/${routine.routine.id}")
                                false // Don't dismiss, just navigate
                            }
                            SwipeToDismissBoxValue.Settled -> false
                        }
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color = when (dismissState.dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                            SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.8f)
                            SwipeToDismissBoxValue.Settled -> Color.Transparent
                        }
                        val alignment = when (dismissState.dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                            SwipeToDismissBoxValue.Settled -> Alignment.Center
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                SwipeToDismissBoxValue.Settled -> { /* No icon */ }
                            }
                        }
                    }
                ) {
                    WorkoutRoutineItem(routine = routine, onClick = { navController.navigate("routine_details/${routine.routine.id}") })
                }
            }
        }
    }
}

@Composable
fun WorkoutRoutineItem(routine: WorkoutRoutineWithExercises, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        onClick = onClick
    ) {
        Text(text = routine.routine.name, modifier = Modifier.padding(16.dp))
    }
}