package mx.utng.srcp.fulbito.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import mx.utng.srcp.fulbito.data.local.entity.EventType
import mx.utng.srcp.fulbito.presentation.screens.*
import mx.utng.srcp.fulbito.presentation.theme.FulbitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FulbitoApp()
        }
    }
}

@Composable
fun FulbitoApp() {
    val navController = rememberSwipeDismissableNavController()
    val viewModel: MainViewModel = viewModel()
    val match by viewModel.match.collectAsState()
    val players by viewModel.players.collectAsState()
    val events by viewModel.events.collectAsState()

    FulbitoTheme {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                DashboardScreen(
                    match = match,
                    onPauseToggle = { viewModel.togglePause() },
                    onFinish = { viewModel.finishMatch() },
                    onNextPeriod = { viewModel.nextPeriod() },
                    onReopenMatch = { viewModel.reopenMatch() },
                    onEventClick = { type ->
                        navController.navigate("registration/$type")
                    },
                    onEditLastEvent = {
                        navController.navigate("log")
                    }
                )
            }

            composable("log") {
                EventLogScreen(
                    events = events,
                    homeTeam = match?.homeTeam ?: "Local",
                    awayTeam = match?.awayTeam ?: "Visita",
                    onEditEvent = { event ->
                        navController.navigate("edit/${event.id}")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                "registration/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeStr = backStackEntry.arguments?.getString("type") ?: EventType.GOAL.name
                val eventType = EventType.valueOf(typeStr)
                EventRegistrationScreen(
                    eventType = eventType,
                    homeTeam = match?.homeTeam ?: "Local",
                    awayTeam = match?.awayTeam ?: "Visita",
                    players = players,
                    events = events,
                    onConfirm = { dorsal, teamId ->
                        viewModel.registerEvent(eventType, dorsal, teamId)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                "edit/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                val event = events.find { it.id == eventId }
                if (event != null) {
                    EditEventScreen(
                        title = "EDITAR ${event.type}",
                        currentTime = formatTime(event.timestampSeconds),
                        teamId = event.teamId,
                        initialDorsal = event.playerDorsal,
                        players = players,
                        onSave = { newDorsal ->
                            viewModel.updateEventById(eventId, newDorsal)
                            navController.popBackStack()
                        },
                        onDelete = {
                            viewModel.deleteEventById(eventId)
                            navController.popBackStack()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }
        }
    }
}
