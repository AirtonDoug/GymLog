package com.example.gymlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToEnd) {
                            myRoutinesViewModel.deleteRoutine(routine.routine.id)
                            true
                        } else if (it == DismissValue.DismissedToStart) {
                            navController.navigate("create_edit_routine/${routine.routine.id}")
                            false
                        } else {
                            false
                        }
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color = when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd -> Color.Red
                            DismissDirection.EndToStart -> Color.Green
                            null -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(16.dp),
                            contentAlignment = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                null -> Alignment.Center
                            }
                        ) {
                            if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            } else if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutRoutineItem(routine: WorkoutRoutineWithExercises, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        onClick = onClick
    ) {
        Text(text = routine.routine.name, modifier = Modifier.padding(16.dp))
    }
}
