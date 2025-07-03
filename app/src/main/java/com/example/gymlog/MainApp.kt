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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.data.repositories.UserPreferences
import com.example.gymlog.data.repositories.UserPreferencesRepository
import com.example.gymlog.ui.navigation.AppNavigation
import com.example.gymlog.ui.theme.AppTheme
import com.example.gymlog.ui.theme.GymLogTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permissão para notificações CONCEDIDA")
        } else {
            Log.d("MainActivity", "Permissão para notificações NEGADA")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Permissão para notificações já foi concedida.")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.d("MainActivity", "Solicitando permissão para notificações.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        // --- LINHA CORRIGIDA ---
        // Em vez de chamar o construtor, pedimos a instância única (Singleton).
        val userPreferencesRepository = UserPreferencesRepository.getInstance(this)

        setContent {
            // Coleta o estado completo das preferências de forma reativa.
            // O valor inicial garante que o app não quebre antes do DataStore carregar.
            val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = UserPreferences(isDarkMode = false, notificationsEnabled = true, appTheme = AppTheme.DEFAULT)
            )

            // Passa as preferências coletadas para o MainApp.
            // Sempre que o userPreferences mudar, o MainApp será recomposto com os novos valores.
            MainApp(
                darkTheme = userPreferences.isDarkMode,
                appTheme = userPreferences.appTheme
            )
        }
    }
}

@Composable
fun MainApp(darkTheme: Boolean, appTheme: AppTheme) {
    // A chamada agora é mais simples e passa apenas os parâmetros que vêm do DataStore.
    // O GymLogTheme agora obedece 100% a estes parâmetros.
    GymLogTheme(darkTheme = darkTheme, appTheme = appTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavigation(
                navController = navController
            )
        }
    }
}