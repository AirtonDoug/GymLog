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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.data.repositories.UserPreferencesRepository
import com.example.gymlog.ui.navigation.AppNavigation
import com.example.gymlog.ui.theme.GymLogTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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

        val userPreferencesRepository = UserPreferencesRepository(this)
        val initialDarkMode = runBlocking { userPreferencesRepository.userPreferencesFlow.first().isDarkMode }


        setContent {
            val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = runBlocking { userPreferencesRepository.userPreferencesFlow.first() }
            )
            MainApp(darkTheme = userPreferences.isDarkMode)
        }
    }
}

@Composable
fun MainApp(darkTheme: Boolean) {
    GymLogTheme(darkTheme = darkTheme) {
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