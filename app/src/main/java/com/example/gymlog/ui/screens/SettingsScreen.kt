package com.example.gymlog.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gymlog.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onClearFavorites: () -> Unit,
    onResetPreferences: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var autoPlayVideos by remember { mutableStateOf(false) }
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
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Seção de Notificações
            SettingsSectionHeader(title = "Notificações")

            // Switch para Notificações
            SettingsSwitchItem(
                title = "Notificações",
                description = "Receber lembretes e atualizações",
                icon = Icons.Default.Notifications,
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Seção de Mídia
            SettingsSectionHeader(title = "Mídia")

            // Switch para Sons
            SettingsSwitchItem(
                title = "Sons",
                description = "Ativar sons de feedback",
                icon = Icons.Default.VolumeUp,
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it }
            )

            // Switch para Reprodução Automática
            SettingsSwitchItem(
                title = "Reprodução Automática",
                description = "Reproduzir vídeos automaticamente",
                icon = Icons.Default.PlayCircle,
                checked = autoPlayVideos,
                onCheckedChange = { autoPlayVideos = it }
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
                    dialogAction = onClearFavorites
                    showConfirmDialog = true
                }
            )

            // Botão para Redefinir Preferências
            SettingsButtonItem(
                title = "Redefinir Preferências",
                description = "Restaurar todas as configurações para o padrão",
                icon = Icons.Default.Refresh,
                onClick = {
                    dialogTitle = "Redefinir Preferências"
                    dialogText = "Tem certeza que deseja redefinir todas as preferências para os valores padrão? Esta ação não pode ser desfeita."
                    dialogAction = onResetPreferences
                    showConfirmDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Informações do aplicativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gym Log",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Versão 1.0.0",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "© 2025 Gym Log App",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Diálogo de confirmação
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
            .padding(vertical = 8.dp),
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
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
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
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
