package com.example.gymlog.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gymlog.data.repositories.MockWorkoutRepository
import com.example.gymlog.data.repositories.UserPreferencesRepository
import com.example.gymlog.ui.components.BottomNavigationBar
import com.example.gymlog.ui.viewmodel.SettingsViewModel
import com.example.gymlog.ui.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            context.applicationContext as Application,
            MockWorkoutRepository(),
            UserPreferencesRepository(context)
        )
    )
    val userPreferences by settingsViewModel.userPreferencesFlow.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var dialogAction by remember { mutableStateOf<() -> Unit>({}) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Seção de Aparência
            SettingsSectionHeader(title = "Aparência")

            // Switch para Modo Escuro
            SettingsSwitchItem(
                title = "Modo Escuro",
                description = "Ativar tema escuro para o aplicativo",
                icon = Icons.Default.DarkMode,
                checked = userPreferences.isDarkMode,
                onCheckedChange = { settingsViewModel.setDarkMode(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Seção de Notificações
            SettingsSectionHeader(title = "Notificações")

            // Switch para Notificações
            SettingsSwitchItem(
                title = "Notificações",
                description = "Receber lembretes e atualizações",
                icon = Icons.Default.Notifications,
                checked = userPreferences.notificationsEnabled,
                onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Seção de Dados
            SettingsSectionHeader(title = "Dados")

            // Botão para Limpar Favoritos
            SettingsButtonItem(
                title = "Limpar Favoritos",
                description = "Remover todos os treinos favoritos",
                icon = Icons.Default.Delete,
                onClick = {
                    dialogTitle = "Limpar Favoritos"
                    dialogText = "Tem certeza que deseja remover todos os treinos favoritos? Esta ação não pode ser desfeita."
                    dialogAction = { settingsViewModel.clearFavorites() }
                    showConfirmDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text(dialogTitle) },
                text = { Text(dialogText) },
                confirmButton = {
                    Button(
                        onClick = {
                            dialogAction()
                            showConfirmDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showConfirmDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCheckedChange(!checked) }, // Permite clicar na linha toda
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium // Ajuste de estilo para melhor visualização
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium, // Ajuste de estilo para melhor visualização
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsButtonItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}