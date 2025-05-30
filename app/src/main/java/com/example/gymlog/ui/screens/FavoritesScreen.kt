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
import com.example.gymlog.ui.viewmodel.FavoritesViewModel
import com.example.gymlog.ui.navigation.FavoritesViewModelFactory // Assuming factory is in navigation package

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    // Inject ViewModel
    favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModelFactory()),
    favoriteWorkouts: Nothing,
    onRemoveFavorite: Nothing
) {
    // Collect state from ViewModel
    val uiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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
        // Handle Loading, Error, and Empty states from ViewModel
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
            uiState.favoriteRoutines.isEmpty() -> {
                // Display empty state message
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Favorite, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nenhum treino favorito", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Adicione treinos aos favoritos para vê-los aqui", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.navigate("home") }) {
                            Icon(Icons.Default.FitnessCenter, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Explorar Treinos")
                        }
                    }
                }
            }
            else -> {
                // Display list of favorites from ViewModel state
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.favoriteRoutines, key = { it.id }) { workout ->
                        FavoriteWorkoutCard(
                            workout = workout,
                            onClick = { navController.navigate("workout_details/${workout.id}") },
                            // Call ViewModel method to remove favorite
                            onRemove = { favoritesViewModel.removeFavorite(workout) }
                        )
                    }
                }
            }
        }
    }
}

// FavoriteWorkoutCard remains the same, receiving data and callbacks
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteWorkoutCard(
    workout: WorkoutRoutine,
    onClick: () -> Unit,
    onRemove: () -> Unit
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
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onRemove,
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
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover dos favoritos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(workout.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(workout.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, "Duração", Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${workout.duration} min", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, "Categoria", Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(workout.category, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, "Dificuldade", Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(workout.difficulty, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
