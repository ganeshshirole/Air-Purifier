package org.kmm.airpurifier

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kmm.airpurifier.presentation.intent.HomeScreenIntent
import org.kmm.airpurifier.presentation.ui.screen.HomeScreen
import org.kmm.airpurifier.presentation.ui.screen.SecondScreen
import org.kmm.airpurifier.presentation.ui.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable(route = "home") {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreen(viewModel.state) {
                    when (it) {
                        is HomeScreenIntent.Navigate -> {
                            navController.navigate(it.routeName)
                        }
                        else -> {
                            viewModel.handleIntent(it)
                        }
                    }
                }
            }
            composable(route = "second") {
                SecondScreen {
                    navController.popBackStack()
                }
            }
        }
    }
}