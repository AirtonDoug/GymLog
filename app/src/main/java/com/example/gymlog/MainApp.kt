package com.example.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.ui.navigation.AppNavigation
import com.example.gymlog.ui.theme.GymLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    // 1. Chame a função @Composable isSystemInDarkTheme() aqui, no escopo do MainApp.
    val systemIsInDarkTheme = isSystemInDarkTheme()

    // 2. Use o resultado (que é um Boolean) para inicializar o mutableStateOf.
    //    O bloco de 'remember' agora usa um valor booleano já resolvido,
    //    e não uma chamada a uma função composable.
    var isDarkTheme by remember { mutableStateOf(systemIsInDarkTheme) }

    GymLogTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavigation(navController = navController)
        }
    }
}