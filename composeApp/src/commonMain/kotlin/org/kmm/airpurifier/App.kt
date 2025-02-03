package org.kmm.airpurifier

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kmm.airpurifier.presentation.ui.screen.HomeScreen
import org.kmm.airpurifier.presentation.ui.screen.SecondScreen
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable(route = "home") {
                    HomeScreen(navController)
                }
                composable(route = "second") {
                    SecondScreen(navController)
                }
            }
        }
    }
}