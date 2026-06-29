package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import mx.utng.srcp.fulbito.data.local.entity.EventType
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity

@Composable
fun DashboardScreen(
    match: MatchEntity?,
    onPauseToggle: () -> Unit,
    onFinish: () -> Unit,
    onNextPeriod: () -> Unit,
    onReopenMatch: () -> Unit,
    onEventClick: (EventType) -> Unit,
    onEditLastEvent: () -> Unit
) {
    val scrollState = rememberScalingLazyListState()
    val isFinished = match?.isFinished == true

    var showFinishConfirmation by remember { mutableStateOf(false) }
    var showNextPeriodConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scrollState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp)
        ) {
            // Timer and Edit access
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditLastEvent() }
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = formatTime(match?.elapsedTimeSeconds ?: 0),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFinished) Color.Red else Color.White
                    )
                    Text(
                        text = if (isFinished) "PARTIDO FINALIZADO" else "TIEMPO DE PARTIDO",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Period Indicator
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .background(
                            color = if (isFinished) Color.DarkGray else Color(0xFF2196F3),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    val periodText = when(match?.currentPeriod) {
                        "1ER TIEMPO" -> "PRIMER TIEMPO"
                        "2DO TIEMPO" -> "SEGUNDO TIEMPO"
                        else -> match?.currentPeriod ?: "---"
                    }
                    Text(
                        text = periodText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Scoreboard
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "${match?.homeScore ?: 0}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(text = "—", fontSize = 20.sp, color = Color.Gray)
                        Text(text = "${match?.awayScore ?: 0}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${match?.homeTeam ?: "Local"} vs ${match?.awayTeam ?: "Visita"}",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Control Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (!isFinished) {
                        SmallControlChip(
                            text = if (match?.isPaused == true) "PLAY" else "PAUSA",
                            onClick = onPauseToggle
                        )
                        SmallControlChip(text = "FIN", onClick = { showFinishConfirmation = true })
                        SmallControlChip(text = "SIG. T", onClick = { showNextPeriodConfirmation = true })
                    } else {
                        CompactChip(
                            onClick = onReopenMatch,
                            label = { Text("REABRIR PARTIDO", fontSize = 8.sp) },
                            colors = ChipDefaults.secondaryChipColors(backgroundColor = Color(0xFF3F51B5))
                        )
                    }
                }
            }

            // Event Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventButton(
                        text = "GOL", 
                        color = if (isFinished) Color.Gray else Color(0xFF4CAF50), 
                        weight = 1f,
                        enabled = !isFinished
                    ) { onEventClick(EventType.GOAL) }
                    
                    EventButton(
                        text = "AMAR", 
                        color = if (isFinished) Color.Gray else Color(0xFFFFC107), 
                        weight = 1f,
                        enabled = !isFinished
                    ) { onEventClick(EventType.YELLOW_CARD) }
                    
                    EventButton(
                        text = "ROJA", 
                        color = if (isFinished) Color.Gray else Color(0xFFF44336), 
                        weight = 1f,
                        enabled = !isFinished
                    ) { onEventClick(EventType.RED_CARD) }
                }
            }
        }
    }

    // Confirmations
    ConfirmationDialog(
        show = showFinishConfirmation,
        title = "¿FINALIZAR PARTIDO?",
        onConfirm = { onFinish(); showFinishConfirmation = false },
        onCancel = { showFinishConfirmation = false }
    )

    ConfirmationDialog(
        show = showNextPeriodConfirmation,
        title = "¿PASAR AL SIG. TIEMPO?",
        onConfirm = { onNextPeriod(); showNextPeriodConfirmation = false },
        onCancel = { showNextPeriodConfirmation = false }
    )
}

@Composable
fun SmallControlChip(text: String, onClick: () -> Unit) {
    CompactChip(
        onClick = onClick,
        label = { Text(text, fontSize = 9.sp, fontWeight = FontWeight.Medium) },
        colors = ChipDefaults.secondaryChipColors(),
        modifier = Modifier.height(28.dp)
    )
}

@Composable
fun RowScope.EventButton(
    text: String, 
    color: Color, 
    weight: Float, 
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(weight).height(36.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color)
    ) {
        Text(text = text, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = if (enabled) Color.White else Color.DarkGray)
    }
}

fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
