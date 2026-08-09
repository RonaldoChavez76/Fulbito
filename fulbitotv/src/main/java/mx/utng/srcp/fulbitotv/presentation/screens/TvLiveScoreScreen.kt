package mx.utng.srcp.fulbitotv.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import mx.utng.srcp.fulbitotv.data.remote.Event
import mx.utng.srcp.fulbitotv.data.remote.Match
import mx.utng.srcp.fulbitotv.data.remote.Team
import androidx.activity.compose.BackHandler

@Composable
fun TvLiveScoreScreen(
    match: Match,
    events: List<Event>,
    players: List<mx.utng.srcp.fulbitotv.data.remote.Player>,
    teams: List<Team>,
    onBack: () -> Unit = {}
) {
    BackHandler {
        onBack()
    }

    val homeTeamInfo = teams.find { it._id == match.homeTeamRef }
    val awayTeamInfo = teams.find { it._id == match.awayTeamRef }

    // Formateo de tiempo
    var currentTimerSeconds by remember(match.elapsedTimeSeconds, match.isPaused) { mutableStateOf(match.elapsedTimeSeconds) }

    LaunchedEffect(match.isPaused, match.elapsedTimeSeconds) {
        if (!match.isPaused) {
            while (true) {
                delay(1000)
                currentTimerSeconds++
            }
        }
    }

    val minutes = currentTimerSeconds / 60
    val seconds = currentTimerSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    val latestGoal = events.lastOrNull { it.type == "GOAL" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FULBITO — TV",
                color = TextGray,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Marcador Central
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Local
            TeamScoreColumn(
                teamName = match.homeTeam ?: "Local",
                score = match.homeScore,
                shieldUrl = homeTeamInfo?.shieldUrl
            )

            // Centro: Tiempo y Periodo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardDark)
                        .padding(horizontal = 48.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = timeString,
                        color = TextYellow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 80.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDC2626)) // Rojo
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = match.currentPeriod.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }

            // Visita
            TeamScoreColumn(
                teamName = match.awayTeam ?: "Visita",
                score = match.awayScore,
                shieldUrl = awayTeamInfo?.shieldUrl
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer: Último Gol y Eventos
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Últimos eventos list
            Column(modifier = Modifier.weight(1f)) {
                SectionHeader("ÚLTIMOS EVENTOS", TextBlue)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val recentEvents = events.sortedByDescending { it.timestampSeconds }
                    items(recentEvents) { event ->
                        val teamName = if (event.teamId == 0) match.homeTeam else match.awayTeam
                        val eventType = if (event.type == "GOAL") "⚽ GOL" else if (event.type.contains("YELLOW") || event.type == "AMARILLA") "🟨 Tarjeta" else "🟥 Tarjeta"
                        val eventColor = if (event.type == "GOAL") TextGreen else if (event.type.contains("YELLOW") || event.type == "AMARILLA") TextYellow else Color.Red
                        
                        val playerInfo = players.find { it.teamId == event.teamId && it.dorsal == event.playerDorsal }
                        val playerName = playerInfo?.name?.ifBlank { "Jugador #${event.playerDorsal}" } ?: "Jugador #${event.playerDorsal}"
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardDark)
                                .focusable()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(eventType, color = eventColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(playerName, color = Color.White, modifier = Modifier.weight(1f))
                            Text(teamName ?: "", color = TextGray, modifier = Modifier.weight(1f))
                            val m = event.timestampSeconds / 60
                            val s = event.timestampSeconds % 60
                            Text(String.format("%02d:%02d", m, s), color = TextGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Destacado: Último Gol
            if (latestGoal != null) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader("ÚLTIMO GOL", TextYellow)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDark)
                            .border(2.dp, TextYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚽", fontSize = 48.sp)
                            Spacer(modifier = Modifier.width(24.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("GOL DE", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                val latestPlayerInfo = players.find { it.teamId == latestGoal.teamId && it.dorsal == latestGoal.playerDorsal }
                                val latestPlayerName = latestPlayerInfo?.name?.ifBlank { "JUGADOR #${latestGoal.playerDorsal}" } ?: "JUGADOR #${latestGoal.playerDorsal}"
                                
                                Text(
                                    text = latestPlayerName.uppercase(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                val teamName = if (latestGoal.teamId == 0) match.homeTeam else match.awayTeam
                                Text(
                                    text = teamName?.uppercase() ?: "",
                                    color = TextYellow,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TeamScoreColumn(teamName: String, score: Int, shieldUrl: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (shieldUrl != null && shieldUrl.isNotBlank()) {
            AsyncImage(
                model = shieldUrl,
                contentDescription = teamName,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, CardDark, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(CardDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = teamName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = teamName.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = score.toString(),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 96.sp
        )
    }
}
