package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.data.remote.Match
import mx.utng.cfga.fulbitoapp.data.remote.Player
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel
import mx.utng.cfga.fulbitoapp.presentation.PlayerViewModel

// ─── Paleta de acento extra ──────────────────────────────────────────────────
private val AccentGold = Color(0xFFFFB300)
private val SurfaceElevated = Color(0xFFFFFFFF)
private val SurfaceSubtle = Color(0xFFF0F4F2)   // verde muy tenue, casi blanco
private val DividerColor = Color(0xFFEEF0EF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDashboardScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    playerViewModel: PlayerViewModel
) {
    val user by loginViewModel.user.collectAsState()
    val captainInfo by playerViewModel.captainInfo.collectAsState()

    LaunchedEffect(user?.id) {
        user?.id?.let { playerViewModel.fetchCaptainInfo(it) }
    }

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = {
                        loginViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        }
    ) { padding ->

        if (captainInfo?.isCaptain == true) {
            // ═══════════════════════════════════════════════
            //   VISTA CAPITÁN
            // ═══════════════════════════════════════════════
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 1. HEADER IDENTIDAD ─────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar con inicial
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(FulbitoGreen, CircleShape)
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.username?.firstOrNull()?.uppercaseChar() ?: 'C').toString(),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Hola, ${user?.username ?: "Capitán"}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = FulbitoTextDark
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(AccentGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .border(1.dp, AccentGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Capitán · ${captainInfo?.team?.name ?: ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBF7800)
                                )
                            }
                        }
                    }
                }

                // ── 2. RESUMEN DE PLANTILLA ──────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272E)), // Dark premium background
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Tamaño de Plantilla",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${captainInfo?.players?.size ?: 0}",
                                        color = Color.White,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "jugadores",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // ── 3. ACCESO RÁPIDO A PERFIL ────────────────────────────────
                item {
                    ActionRow(
                        icon = Icons.Default.Person,
                        iconBg = FulbitoGreen.copy(alpha = 0.08f),
                        iconTint = FulbitoGreen,
                        title = "Mi Perfil",
                        subtitle = "Ver mis estadísticas y cuenta",
                        onClick = { navController.navigate("player_profile") }
                    )
                }

                // ── 4. PRÓXIMOS PARTIDOS ─────────────────────────────────────
                item {
                    SectionTitle(text = "Próximos Partidos")
                }

                if (captainInfo?.upcomingMatches.isNullOrEmpty()) {
                    item {
                        EmptyStateSlot(text = "No hay partidos programados próximamente.")
                    }
                } else {
                    items(captainInfo?.upcomingMatches ?: emptyList()) { match ->
                        ModernMatchCard(match = match)
                    }
                }

                // ── 5. PLANTILLA ─────────────────────────────────────────────
                item {
                    SectionTitle(text = "Plantilla del Equipo")
                }

                if (captainInfo?.players.isNullOrEmpty()) {
                    item {
                        EmptyStateSlot(text = "No hay jugadores registrados en el equipo.")
                    }
                } else {
                    items(captainInfo?.players ?: emptyList()) { player ->
                        ModernPlayerRow(player = player)
                    }
                }
            }

        } else {
            // ═══════════════════════════════════════════════
            //   VISTA JUGADOR NORMAL
            // ═══════════════════════════════════════════════
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(FulbitoGreen, CircleShape)
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.username?.firstOrNull()?.uppercaseChar() ?: 'J').toString(),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Hola, ${user?.username ?: "Jugador"}!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = FulbitoTextDark
                            )
                            Text(
                                text = "Bienvenido a tu panel",
                                color = FulbitoTextLight,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Acceso a perfil
                item {
                    ActionRow(
                        icon = Icons.Default.Person,
                        iconBg = FulbitoGreen.copy(alpha = 0.08f),
                        iconTint = FulbitoGreen,
                        title = "Mi Perfil",
                        subtitle = "Estadísticas, historial y contraseña",
                        onClick = { navController.navigate("player_profile") }
                    )
                }

                // Bloque informativo del equipo vacío
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceSubtle),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(FulbitoGreen.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Panel Para Jugadores", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FulbitoTextDark)
                                Text("Consulta a Tu Capitán para notificarte el siguiente partido.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── COMPONENTES DE DISEÑO ───────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = FulbitoTextDark,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}



@Composable
private fun ActionRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).background(iconBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FulbitoTextDark)
                Text(subtitle, fontSize = 12.sp, color = FulbitoTextLight, modifier = Modifier.padding(top = 2.dp))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun EmptyStateSlot(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceSubtle, RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(16.dp))
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
private fun ModernMatchCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de partido
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(FulbitoGreen.copy(alpha = 0.08f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${match.homeTeam} vs ${match.awayTeam}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = FulbitoTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    match.fecha?.let {
                        DotLabel(text = it, bg = Color(0xFFE8F5E9), fg = FulbitoGreen)
                    }
                    match.hora?.let {
                        DotLabel(text = it, bg = Color(0xFFE3F2FD), fg = Color(0xFF1565C0))
                    }
                }
                match.cancha?.let {
                    Text("📍 $it", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun DotLabel(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp, color = fg, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ModernPlayerRow(player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceElevated, RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de dorsal
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(FulbitoGreen.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "#${player.dorsal ?: "?"}",
                color = FulbitoGreen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    player.name ?: "Sin Nombre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = FulbitoTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (player.isCaptain) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Capitán",
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                player.position ?: "Jugador",
                color = FulbitoTextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}
