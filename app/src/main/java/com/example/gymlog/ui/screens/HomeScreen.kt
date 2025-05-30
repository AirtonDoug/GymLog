package com.example.gymlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel
import androidx.navigation.NavController
import com.example.gymlog.models.WorkoutRoutine
import com.example.gymlog.ui.components.BottomNavigationBar
import com.example.gymlog.ui.components.StatRowSmall // Assuming StatRowSmall is defined
import com.example.gymlog.ui.viewmodel.HomeViewModel
import com.example.gymlog.data.repositories.MockWorkoutRepository // Temp import for factory

// Basic ViewModel Factory (replace with proper DI later)
class HomeViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // In a real app, inject the repository dependency here
            return HomeViewModel(MockWorkoutRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // Inject the ViewModel
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory())
) {
    // Collect state from ViewModel
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gym Log") },
                actions = {
                    // Search Field - Use ViewModel state and callback
                    OutlinedTextField(
                        value = searchQuery, // Use state from ViewModel
                        onValueChange = { homeViewModel.onSearchQueryChange(it) }, // Call ViewModel method
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Buscar rotinas...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { homeViewModel.onSearchQueryChange("") }) { // Call ViewModel method
                                    Icon(Icons.Default.Clear, "Limpar busca")
                                }
                            }
                        }
                    )
                    // More Options Menu
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Favoritos") }, leadingIcon = { Icon(Icons.Default.Favorite, null) }, onClick = { navController.navigate("favorites"); showMenu = false })
                        DropdownMenuItem(text = { Text("Configurações") }, leadingIcon = { Icon(Icons.Default.Settings, null) }, onClick = { navController.navigate("settings"); showMenu = false })
                        DropdownMenuItem(text = { Text("Ajuda") }, leadingIcon = { Icon(Icons.Default.Help, null) }, onClick = { navController.navigate("help"); showMenu = false })
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Handle Loading and Error states
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
            else -> {
                // Display list when data is available
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.routines, key = { it.id }) { routine ->
                        WorkoutCard(
                            workout = routine,
                            // isFavorite is now part of the WorkoutRoutine from the ViewModel/Repository flow
                            isFavorite = routine.isFavorite,
                            // Pass the ViewModel toggle function directly
                            onToggleFavorite = { homeViewModel.toggleFavorite(routine) },
                            onClick = { navController.navigate("workout_details/${routine.id}") }
                        )
                    }
                }
            }
        }
    }
}

// WorkoutCard remains largely the same, receiving isFavorite and onToggleFavorite
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCard(
    workout: WorkoutRoutine,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = workout.image),
                    contentDescription = workout.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatRowSmall(icon = Icons.Default.Timer, value = "${workout.duration} min")
                    StatRowSmall(icon = Icons.Default.FitnessCenter, value = workout.category)
                    StatRowSmall(icon = Icons.Default.Speed, value = workout.difficulty)
                }
            }
        }
    }
}
