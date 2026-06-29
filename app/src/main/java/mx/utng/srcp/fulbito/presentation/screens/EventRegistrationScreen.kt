package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.EventType
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity

@Composable
fun EventRegistrationScreen(
    eventType: EventType,
    homeTeam: String,
    awayTeam: String,
    players: List<PlayerEntity>,
    events: List<EventEntity>,
    onConfirm: (String, Int) -> Unit,
    onCancel: () -> Unit
) {
    var selectedTeamId by remember { mutableStateOf(0) }
    var selectedDorsal by remember { mutableStateOf("") }
    var isManualMode by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val filteredPlayers = players.filter { it.teamId == selectedTeamId }.toMutableList()
    if (selectedDorsal.isNotEmpty() && filteredPlayers.none { it.dorsal == selectedDorsal }) {
        filteredPlayers.add(PlayerEntity(id = "manual", dorsal = selectedDorsal, name = "", teamId = selectedTeamId))
    }

    if (isManualMode) {
        ManualInputView(
            onDorsalSelected = { 
                selectedDorsal = it
                isManualMode = false 
            },
            onCancel = { isManualMode = false }
        )
    } else {
        val scrollState = rememberScalingLazyListState()
        val eventName = when (eventType) {
            EventType.GOAL -> "GOL"
            EventType.YELLOW_CARD -> "AMARILLA"
            EventType.RED_CARD -> "ROJA"
        }
        val headerColor = when (eventType) {
            EventType.GOAL -> Color(0xFF4CAF50)
            EventType.YELLOW_CARD -> Color(0xFFFFC107)
            EventType.RED_CARD -> Color(0xFFF44336)
        }

        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "REGISTRAR $eventName",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = headerColor
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ToggleButton(
                            checked = selectedTeamId == 0,
                            onCheckedChange = { selectedTeamId = 0 },
                            modifier = Modifier.weight(1f).height(30.dp).padding(horizontal = 2.dp)
                        ) {
                            Text(homeTeam.take(6), fontSize = 8.sp)
                        }
                        ToggleButton(
                            checked = selectedTeamId == 1,
                            onCheckedChange = { selectedTeamId = 1 },
                            modifier = Modifier.weight(1f).height(30.dp).padding(horizontal = 2.dp)
                        ) {
                            Text(awayTeam.take(6), fontSize = 8.sp)
                        }
                    }
                }
            }

            item {
                // Dorsal Grid using flow layout logic
                Column(modifier = Modifier.fillMaxWidth()) {
                    val rows = filteredPlayers.chunked(3)
                    rows.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            row.forEach { player ->
                                val playerEvents = events.filter { it.playerDorsal == player.dorsal && it.teamId == player.teamId }
                                val yellowCards = playerEvents.count { it.type == EventType.YELLOW_CARD }
                                val hasRedCard = playerEvents.any { it.type == EventType.RED_CARD }
                                val isExpelled = hasRedCard || yellowCards >= 2
                                val isSelected = selectedDorsal == player.dorsal
                                
                                Button(
                                    onClick = { if (!isExpelled) selectedDorsal = player.dorsal },
                                    enabled = !isExpelled,
                                    modifier = Modifier.size(36.dp).padding(2.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = when {
                                            isExpelled -> Color.Black
                                            isSelected -> headerColor
                                            else -> Color.DarkGray
                                        },
                                        contentColor = if (isExpelled) Color.Red else Color.White
                                    )
                                ) {
                                    Text(
                                        text = player.dorsal, 
                                        fontSize = 12.sp,
                                        textDecoration = if (isExpelled) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    )
                                }
                            }
                            if (row.size < 3) {
                                Button(
                                    onClick = { isManualMode = true },
                                    modifier = Modifier.size(36.dp).padding(2.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                                ) {
                                    Text("+", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                    if (filteredPlayers.size % 3 == 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = { isManualMode = true },
                                modifier = Modifier.size(36.dp).padding(2.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                            ) {
                                Text("+", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactChip(
                        onClick = onCancel,
                        label = { Text("CAN", fontSize = 8.sp) },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                    CompactChip(
                        onClick = { if (selectedDorsal.isNotEmpty()) showConfirmDialog = true },
                        label = { Text("LISTO ${selectedDorsal}", fontSize = 8.sp) },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(0xFF4CAF50)),
                        enabled = selectedDorsal.isNotEmpty()
                    )
                }
            }
        }

        val teamName = if (selectedTeamId == 0) homeTeam else awayTeam
        ConfirmationDialog(
            show = showConfirmDialog,
            title = "¿REGISTRAR $eventName A #$selectedDorsal DE $teamName?",
            onConfirm = { onConfirm(selectedDorsal, selectedTeamId); showConfirmDialog = false },
            onCancel = { showConfirmDialog = false }
        )
    }
}

@Composable
fun ManualInputView(onDorsalSelected: (String) -> Unit, onCancel: () -> Unit) {
    var dorsal by remember { mutableStateOf("") }
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp)
    ) {
        item {
            Text("DORSAL MANUAL", fontSize = 10.sp, color = Color.Gray)
        }
        item {
            Text(text = dorsal.ifEmpty { "--" }, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
        }
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
                numbers.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        row.forEach { label ->
                            Button(
                                onClick = {
                                    when (label) {
                                        "C" -> dorsal = ""
                                        "OK" -> if (dorsal.isNotEmpty()) onDorsalSelected(dorsal)
                                        else -> if (dorsal.length < 2) dorsal += label
                                    }
                                },
                                modifier = Modifier.size(34.dp).padding(2.dp)
                            ) {
                                Text(label, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
        item {
            CompactChip(
                onClick = onCancel,
                label = { Text("VOLVER", fontSize = 8.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
