package mx.utng.srcp.fulbitotv.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.utng.srcp.fulbitotv.data.remote.Match
import mx.utng.srcp.fulbitotv.data.remote.TopScorer

val BgDark = Color(0xFF0F172A)
val CardDark = Color(0xFF1E293B)
val TextYellow = Color(0xFFFACC15)
val TextGreen = Color(0xFF4ADE80)
val TextBlue = Color(0xFF60A5FA)
val TextGray = Color(0xFF94A3B8)

@Composable
fun TvWaitingScreen(
    matches: List<Match>,
    topScorers: List<TopScorer>,
    onMatchClick: (String) -> Unit
) {
    // Mostrar todos los partidos ordenados por fecha y hora
    // Si la fecha está en formato dd/MM/yyyy, la sorteamos como string (funciona para partidos del mismo mes)
    val upcomingMatches = matches.sortedWith(compareBy({ it.fecha }, { it.hora }))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(TextYellow))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROX. PARTIDO: ${upcomingMatches.firstOrNull()?.hora ?: "--:--"}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            Text(
                text = "FULBITO — SALA DE ESPERA",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )

            Text(
                text = "Fulbito TV", // TODO: Dynamic if needed
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Left Column: Tabla de Goleo
            Column(modifier = Modifier.weight(1f)) {
                SectionHeader("TABLA DE GOLEO", TextYellow)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("#", color = TextGray, modifier = Modifier.weight(0.5f), fontSize = 14.sp)
                    Text("Jugador", color = TextGray, modifier = Modifier.weight(2f), fontSize = 14.sp)
                    Text("Equipo", color = TextGray, modifier = Modifier.weight(1.5f), fontSize = 14.sp)
                    Text("Goles", color = TextGray, modifier = Modifier.weight(0.5f), fontSize = 14.sp)
                }
                
                if (topScorers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDark)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no hay goles registrados.\nUsa la app del reloj para registrar goles.",
                            color = TextGray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(topScorers.withIndex().toList()) { (index, scorer) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardDark)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", color = TextYellow, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                                Text(scorer.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                                Text(scorer.team, color = TextGray, modifier = Modifier.weight(1.5f))
                                Box(
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(scorer.goals.toString(), color = TextYellow, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Right Column: Partidos
            Column(modifier = Modifier.weight(1f)) {
                SectionHeader("CALENDARIO DE PARTIDOS", TextGreen)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val nextMatch = upcomingMatches.firstOrNull { !it.isFinished }
                    items(upcomingMatches) { match ->
                        val isNext = match == nextMatch
                        val borderColor = when {
                            isNext -> TextBlue
                            match.isFinished -> Color.Transparent
                            else -> Color.Transparent
                        }
                        val bgColor = if (match.isFinished) Color(0xFF1A1F2E) else CardDark

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable { if (!match.isFinished) match._id?.let { onMatchClick(it) } }
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            match.hora ?: "",
                                            color = if (match.isFinished) TextGray else Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 22.sp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        if (match.isFinished) {
                                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF374151)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                                                Text("FINALIZADO", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else if (isNext) {
                                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF1E3A8A)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                                                Text("SIGUIENTE", color = TextBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text("Cancha ${match.cancha ?: "1"}", color = TextGray, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                if (match.isFinished) {
                                    // Mostrar marcador final
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(match.homeTeam ?: "", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${match.homeScore} - ${match.awayScore}",
                                            color = TextYellow,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(match.awayTeam ?: "", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(
                                        text = "${match.homeTeam}  vs  ${match.awayTeam}",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dotColor))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = TextGray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
