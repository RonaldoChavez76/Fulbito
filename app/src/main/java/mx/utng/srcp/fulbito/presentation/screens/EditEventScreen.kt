package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import mx.utng.srcp.fulbito.data.local.entity.PlayerEntity

@Composable
fun EditEventScreen(
    title: String,
    currentTime: String,
    teamId: Int,
    initialDorsal: String,
    players: List<PlayerEntity>,
    onSave: (String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    var selectedDorsal by remember { mutableStateOf(initialDorsal) }
    var isManualMode by remember { mutableStateOf(false) }

    val filteredPlayers = players.filter { it.teamId == teamId }

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
        
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    Text(text = currentTime, fontSize = 9.sp, color = Color.Gray)
                }
            }

            item {
                Text(text = "¿CORREGIR DORSAL?", fontSize = 10.sp, modifier = Modifier.padding(vertical = 4.dp))
            }

            // Grid
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp,
                    alignment = Alignment.CenterHorizontally
                ) {
                    filteredPlayers.forEach { player ->
                        val isSelected = selectedDorsal == player.dorsal
                        Button(
                            onClick = { selectedDorsal = player.dorsal },
                            modifier = Modifier.size(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isSelected) Color(0xFFFF9800) else Color.DarkGray
                            )
                        ) {
                            Text(text = player.dorsal, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = { isManualMode = true },
                        modifier = Modifier.size(36.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text(text = "+", fontSize = 14.sp)
                    }
                }
            }

            // Bottom Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactChip(
                        onClick = onDelete,
                        label = { Text("BORRAR", fontSize = 7.sp) },
                        colors = ChipDefaults.secondaryChipColors(backgroundColor = Color(0xFFB71C1C)),
                        modifier = Modifier.weight(1f)
                    )
                    CompactChip(
                        onClick = onCancel,
                        label = { Text("CAN", fontSize = 7.sp) },
                        colors = ChipDefaults.secondaryChipColors(),
                        modifier = Modifier.weight(0.8f)
                    )
                    CompactChip(
                        onClick = { if (selectedDorsal.isNotEmpty()) onSave(selectedDorsal) },
                        label = { Text("LISTO", fontSize = 7.sp) },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(0xFFFF9800)),
                        enabled = selectedDorsal.isNotEmpty(),
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}
