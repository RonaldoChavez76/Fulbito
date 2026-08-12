package mx.utng.cfga.fulbitoapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel
import mx.utng.cfga.fulbitoapp.presentation.screens.AdminDashboardScreen
import mx.utng.cfga.fulbitoapp.presentation.screens.AdminMatchScreen
import mx.utng.cfga.fulbitoapp.presentation.screens.AdminPlayerScreen
import mx.utng.cfga.fulbitoapp.presentation.screens.AdminTeamScreen
import mx.utng.cfga.fulbitoapp.presentation.screens.LoginScreen

/**
 * Componente principal de navegación de la aplicación móvil (Jetpack Navigation Compose).
 * 
 * Gestiona el flujo de pantallas y las rutas del usuario. Inicializa los ViewModels
 * compartidos (`LoginViewModel`, `AdminViewModel`, `PlayerViewModel`) y pasa 
 * el estado a cada pantalla según corresponda.
 */
@Composable
fun AppNavigation(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val navController = rememberNavController()
    val adminViewModel: AdminViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val playerViewModel: mx.utng.cfga.fulbitoapp.presentation.PlayerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { role ->
                    if (role == "Admin") {
                        adminViewModel.reloadAll()
                        navController.navigate("admin_league_selection") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (role == "Jugador") {
                        navController.navigate("player_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("admin_league_selection") {
            mx.utng.cfga.fulbitoapp.presentation.screens.LeagueSelectionScreen(
                navController = navController,
                adminViewModel = adminViewModel,
                loginViewModel = loginViewModel,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
        composable("admin_dashboard") {
            AdminDashboardScreen(
                navController = navController,
                viewModel = adminViewModel,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
        composable("admin_teams") {
            AdminTeamScreen(navController = navController, viewModel = adminViewModel)
        }
        composable("admin_players") {
            AdminPlayerScreen(navController = navController, viewModel = adminViewModel)
        }
        composable("admin_matches") {
            AdminMatchScreen(navController = navController, viewModel = adminViewModel)
        }
        composable("player_dashboard") {
            mx.utng.cfga.fulbitoapp.presentation.screens.PlayerDashboardScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                playerViewModel = playerViewModel
            )
        }
        composable("player_profile") {
            mx.utng.cfga.fulbitoapp.presentation.screens.PlayerProfileScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                playerViewModel = playerViewModel
            )
        }
    }
}
