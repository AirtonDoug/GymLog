package com.example.gymlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            // Elevate the theme state management here
            var isDarkTheme by remember { mutableStateOf(false) } // Start with light or check system preference

            // Provide a function to toggle the theme
            val toggleTheme: () -> Unit = { isDarkTheme = !isDarkTheme }

            // Pass the state and the toggle function down
            MainApp(darkTheme = isDarkTheme, onThemeToggle = toggleTheme)
        }
    }
}

@Composable
fun MainApp(darkTheme: Boolean, onThemeToggle: () -> Unit) {
    // Apply the theme based on the state passed from MainActivity
    GymLogTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            // Pass the theme state and toggle function to the Navigation composable
            AppNavigation(
                navController = navController,
                isDarkTheme = darkTheme,
                onThemeToggle = onThemeToggle
            )
        }
    }
}
