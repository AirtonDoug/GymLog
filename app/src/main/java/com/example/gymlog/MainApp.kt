package com.example.gymlog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.ui.navigation.AppNavigation
import com.example.gymlog.ui.theme.GymLogTheme

class MainActivity : ComponentActivity() {

    // --- Início da Modificação ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permissão para notificações CONCEDIDA")
        } else {
            Log.d("MainActivity", "Permissão para notificações NEGADA")
            // Opcional: Mostrar um snackbar ou diálogo explicando por que a permissão é necessária.
        }
    }

    private fun askNotificationPermission() {
        // Válido apenas para Android 13 (API 33) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permissão já concedida
                    Log.d("MainActivity", "Permissão para notificações já foi concedida.")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Opcional: Mostrar uma UI explicando a necessidade da permissão
                    // antes de pedir novamente.
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Solicita a permissão diretamente
                    Log.d("MainActivity", "Solicitando permissão para notificações.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    // --- Fim da Modificação ---


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- Adicionado aqui ---
        askNotificationPermission()
        // --- Fim da adição ---

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val toggleTheme: () -> Unit = { isDarkTheme = !isDarkTheme }
            MainApp(darkTheme = isDarkTheme, onThemeToggle = toggleTheme)
        }
    }
}

@Composable
fun MainApp(darkTheme: Boolean, onThemeToggle: () -> Unit) {
    GymLogTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavigation(
                navController = navController,
                isDarkTheme = darkTheme,
                onThemeToggle = onThemeToggle
            )
        }
    }
}