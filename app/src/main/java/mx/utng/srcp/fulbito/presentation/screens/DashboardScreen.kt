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
    onEventClick: (EventType) -> Unit,
    onEditLastEvent: () -> Unit
) {
    val scrollState = rememberScalingLazyListState()
    val isFinished = match?.isFinished == true

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
                            color = if (isFinished) Color.DarkGray else MaterialTheme.colors.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = match?.currentPeriod ?: "1ER TIEMPO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFinished) Color.White else Color.Black
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

            // Control Buttons (Disabled if finished)
            if (!isFinished) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SmallControlChip(
                            text = if (match?.isPaused == true) "PLAY" else "PAUSA",
                            onClick = onPauseToggle
                        )
                        SmallControlChip(text = "FIN", onClick = onFinish)
                        SmallControlChip(text = "SIG. T", onClick = onNextPeriod)
                    }
                }
            }

            // Event Buttons (Disabled if finished)
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
