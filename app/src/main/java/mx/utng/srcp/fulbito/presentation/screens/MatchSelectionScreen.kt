package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.*
import mx.utng.srcp.fulbito.data.local.entity.MatchEntity

@Composable
fun MatchSelectionScreen(
    matches: List<MatchEntity>,
    isLoading: Boolean,
    onMatchSelected: (String) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                indicatorColor = Color(0xFF4CAF50),
                trackColor = Color(0xFF1B5E20)
            )
        }
        return
    }

    if (matches.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "Sin partidos\nprogramados",
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        return
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp, start = 8.dp, end = 8.dp)
    ) {
        item {
            Text(
                text = "Elige un Partido",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(matches) { match ->
            TitleCard(
                onClick = { match.id?.let { onMatchSelected(it) } },
                title = {
                    Text(
                        text = "${match.homeTeam}\nvs\n${match.awayTeam}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                time = {
                    Text(
                        text = match.hora ?: "",
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                backgroundPainter = CardDefaults.cardBackgroundPainter(
                    startBackgroundColor = Color(0xFF1E1E1E),
                    endBackgroundColor = Color(0xFF121212)
                ),
                contentColor = Color.LightGray,
                titleColor = Color.White
            ) {
                Column(modifier = Modifier.padding(top = 2.dp)) {
                    if (!match.fecha.isNullOrBlank()) {
                        Text(
                            text = match.fecha,
                            fontSize = 10.sp,
                            color = Color(0xFFBDBDBD)
                        )
                    }
                    if (!match.cancha.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📍 ${match.cancha}",
                            fontSize = 10.sp,
                            color = Color(0xFF90CAF9),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
