// Pantalla de: AdminDashboardScreen
package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val teams by viewModel.teams.collectAsState()
    val players by viewModel.players.collectAsState()
    val matches by viewModel.matches.collectAsState()

    val bgColor = if (isDarkMode) Color(0xFF121212) else FulbitoScreenBg
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else FulbitoCardBg
    val textPrimary = if (isDarkMode) Color.White else FulbitoTextDark
    val textSecondary = if (isDarkMode) Color(0xFFAAAAAA) else FulbitoTextLight

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.SportsSoccer,
                            contentDescription = "Logo",
                            tint = FulbitoGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Fulbito",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = FulbitoGreen
                        )
                    }
                },
                actions = {
                    // Botón Modo Noche
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF2E2E2E) else FulbitoLightGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Modo Noche",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    // Botón Volver a Ligas
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF2E2E2E) else FulbitoLightGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver a Ligas", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // --- Tarjeta de sesión activa ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = FulbitoGreen.copy(alpha = 0.5f))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(FulbitoGreen, Color(0xFF0F2618)) // Usando un verde aún más oscuro para el gradiente
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = 180.dp, y = (-40).dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                )
                Box(
                    modifier = Modifier
                        .offset(x = 220.dp, y = 60.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                )
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, FulbitoLightGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("¡Hola de nuevo!", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Administrador", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.2f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF69F0AE)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ONLINE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // --- Estadísticas Rápidas (Datos Reales) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Equipos", value = "${teams.size}", icon = Icons.Outlined.Shield, iconTint = Color(0xFF4CAF50), iconBg = Color(0xFFE8F5E9), cardColor = cardColor, textPrimary = textPrimary, textSecondary = textSecondary)
                StatCard(modifier = Modifier.weight(1f), title = "Jugadores", value = "${players.size}", icon = Icons.Outlined.Groups, iconTint = Color(0xFF2196F3), iconBg = Color(0xFFE3F2FD), cardColor = cardColor, textPrimary = textPrimary, textSecondary = textSecondary)
                StatCard(modifier = Modifier.weight(1f), title = "Partidos", value = "${matches.size}", icon = Icons.Outlined.EventAvailable, iconTint = Color(0xFFFF9800), iconBg = Color(0xFFFFF3E0), cardColor = cardColor, textPrimary = textPrimary, textSecondary = textSecondary)
            }

            // --- Gestión (NUEVO DISEÑO EN GRID) ---
            DashboardSectionLabel("HERRAMIENTAS DE GESTIÓN")

            // Tarjeta Hero: Gestión de Partidos (Ancho completo)
            DashboardHeroCard(
                title = "Gestión de Partidos",
                subtitle = "Programa, edita y actualiza los marcadores en tiempo real.",
                icon = Icons.Outlined.SportsSoccer,
                cardColor = cardColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onClick = { navController.navigate("admin_matches") }
            )

            // Fila con dos tarjetas cuadradas: Equipos y Jugadores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardSquareCard(
                    modifier = Modifier.weight(1f),
                    title = "Directorio de Equipos",
                    icon = Icons.Outlined.Shield,
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = { navController.navigate("admin_teams") }
                )

                DashboardSquareCard(
                    modifier = Modifier.weight(1f),
                    title = "Plantillas de Jugadores",
                    icon = Icons.Outlined.Groups,
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = { navController.navigate("admin_players") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 11.sp, color = textSecondary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DashboardSectionLabel(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp, top = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(16.dp)
                .background(FulbitoGreen, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = FulbitoGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
    }
}

// Nueva tarjeta ancha principal (Hero)
@Composable
private fun DashboardHeroCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(FulbitoLightGreen, FulbitoLightGreen.copy(alpha = 0.3f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = FulbitoGreen,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Nueva tarjeta cuadrada (Grid)
@Composable
private fun DashboardSquareCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Para que sea cuadrada
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(FulbitoLightGreen.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FulbitoGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

