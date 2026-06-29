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
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import mx.utng.srcp.fulbito.data.local.entity.EventEntity
import mx.utng.srcp.fulbito.data.local.entity.EventType

@Composable
fun EventLogScreen(
    events: List<EventEntity>,
    homeTeam: String,
    awayTeam: String,
    onEditEvent: (EventEntity) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp)
    ) {
        item {
            Text("HISTORIAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        if (events.isEmpty()) {
            item {
                Text("No hay eventos", fontSize = 10.sp, modifier = Modifier.padding(top = 20.dp))
            }
        }

        items(events) { event ->
            val teamName = if (event.teamId == 0) homeTeam else awayTeam
            EventItem(event = event, teamName = teamName, onEdit = { onEditEvent(event) })
        }

        item {
            CompactChip(
                onClick = onBack,
                label = { Text("VOLVER", fontSize = 9.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun EventItem(event: EventEntity, teamName: String, onEdit: () -> Unit) {
    val color = when (event.type) {
        EventType.GOAL -> Color(0xFF4CAF50)
        EventType.YELLOW_CARD -> Color(0xFFFFC107)
        EventType.RED_CARD -> Color(0xFFF44336)
    }
    
    val typeName = when (event.type) {
        EventType.GOAL -> "GOL"
        EventType.YELLOW_CARD -> "AMARILLA"
        EventType.RED_CARD -> "ROJA"
    }

    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = color.copy(alpha = 0.2f),
            endBackgroundColor = Color.DarkGray.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = typeName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = "Dorsal: #${event.playerDorsal}", fontSize = 11.sp)
                Text(text = teamName, fontSize = 8.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
            }
            Text(text = formatTime(event.timestampSeconds), fontSize = 9.sp, color = Color.Gray)
        }
    }
}
