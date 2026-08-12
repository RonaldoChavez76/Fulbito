// Pantalla de: LeagueSelectionScreen
package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.data.remote.League
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel

// Paleta de colores de acento por liga (rotan automáticamente)
private val leagueAccentColors = listOf(
    Color(0xFF4CAF50),
    Color(0xFF2196F3),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueSelectionScreen(
    navController: NavController,
    adminViewModel: AdminViewModel,
    loginViewModel: LoginViewModel,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    val leagues by adminViewModel.leagues.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var leagueToEdit by remember { mutableStateOf<League?>(null) }
    var leagueToDelete by remember { mutableStateOf<League?>(null) }

    val bgColor = if (isDarkMode) Color(0xFF121212) else FulbitoScreenBg
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else FulbitoCardBg
    val textPrimary = if (isDarkMode) Color.White else FulbitoTextDark
    val textSecondary = if (isDarkMode) Color(0xFFAAAAAA) else FulbitoTextLight

    LaunchedEffect(Unit) {
        adminViewModel.fetchLeagues()
    }

    if (showCreateDialog || leagueToEdit != null) {
        LeagueFormDialog(
            initialLeague = leagueToEdit,
            isDarkMode = isDarkMode,
            onDismiss = {
                showCreateDialog = false
                leagueToEdit = null
            },
            onSave = { name, desc ->
                if (leagueToEdit != null) {
                    val updatedLeague = leagueToEdit!!.copy(name = name, description = desc)
                    adminViewModel.updateLeague(updatedLeague._id ?: "", updatedLeague) {
                        leagueToEdit = null
                    }
                } else {
                    adminViewModel.createLeague(League(name = name, description = desc)) {
                        showCreateDialog = false
                    }
                }
            }
        )
    }

    if (leagueToDelete != null) {
        AlertDialog(
            onDismissRequest = { leagueToDelete = null },
            title = { Text("Eliminar Liga") },
            text = { Text("¿Estás seguro de que deseas eliminar la liga '${leagueToDelete?.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        leagueToDelete?._id?.let { adminViewModel.deleteLeague(it) }
                        leagueToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { leagueToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.SportsSoccer,
                            contentDescription = "Fulbito",
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
                    IconButton(
                        onClick = {
                            loginViewModel.logout()
                            navController.navigate("login") { popUpTo(0) }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF2E2E2E) else FulbitoLightGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = FulbitoGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Liga")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Liga", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->

        if (leagues.isEmpty()) {
            // --- ESTADO VACÍO PREMIUM ---
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(16.dp, CircleShape, spotColor = FulbitoGreen.copy(alpha = 0.4f))
                        .background(
                            brush = Brush.radialGradient(
                                listOf(FulbitoGreen.copy(alpha = 0.25f), FulbitoGreen.copy(alpha = 0.05f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = FulbitoGreen,
                        modifier = Modifier.size(65.dp)
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Text("¡Bienvenido a Fulbito!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = textPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Aún no tienes ligas creadas.\nToca el botón inferior para crear tu primera liga\ny comenzar a organizar torneos.",
                    color = textSecondary, textAlign = TextAlign.Center, lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = FulbitoGreen.copy(alpha = 0.1f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear mi primera liga", color = FulbitoGreen, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        } else {
            // --- CONTENIDO PRINCIPAL ---
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {

                // === BANNER HERO ===
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = FulbitoGreen.copy(alpha = 0.5f))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(FulbitoGreen, Color(0xFF2D6E3F))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier.offset(x = 200.dp, y = (-30).dp)
                                .size(120.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))
                        )
                        Box(
                            modifier = Modifier.offset(x = 240.dp, y = 50.dp)
                                .size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f))
                        )
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Panel de Ligas", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                    Text("Administra y organiza tus torneos", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HeroBannerStat(modifier = Modifier.weight(1f), label = "Ligas Activas", value = "${leagues.size}")
                                HeroBannerStat(modifier = Modifier.weight(1f), label = "Total Creadas", value = "${leagues.size}")
                            }
                        }
                    }
                }

                // === ENCABEZADO SECCIÓN ===
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tus Ligas",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )
                            Text(
                                text = "${leagues.size} liga${if (leagues.size != 1) "s" else ""} registrada${if (leagues.size != 1) "s" else ""}",
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        }
                        Surface(shape = CircleShape, color = FulbitoGreen) {
                            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${leagues.size}",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }

                // === TARJETAS TIPO POSTER ===
                itemsIndexed(leagues) { index, league ->
                    val accentColor = leagueAccentColors[index % leagueAccentColors.size]
                    LeaguePosterCard(
                        league = league,
                        accentColor = accentColor,
                        cardColor = cardColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClick = {
                            adminViewModel.selectLeague(league)
                            navController.navigate("admin_dashboard")
                        },
                        onEdit = { leagueToEdit = league },
                        onDelete = { leagueToDelete = league }
                    )
                }
            }
        }
    }
}

// =====================================================================
//  HeroBannerStat
// =====================================================================
@Composable
private fun HeroBannerStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.15f)) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium, fontSize = 11.sp)
        }
    }
}

// =====================================================================
//  LeaguePosterCard — Diseño tipo "Cartel de Torneo"
// =====================================================================
@Composable
fun LeaguePosterCard(
    league: League,
    accentColor: Color,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            // ===== HEADER CON GRADIENTE =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(accentColor, accentColor.copy(alpha = 0.55f)),
                            start = Offset(0f, 0f),
                            end = Offset(900f, 450f)
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                // Decoraciones geométricas
                Box(modifier = Modifier.offset(x = (-35).dp, y = (-35).dp).size(120.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)))
                Box(modifier = Modifier.offset(x = 210.dp, y = 55.dp).size(100.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)))
                Box(modifier = Modifier.offset(x = 290.dp, y = (-20).dp).size(65.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)))

                // Contenido del header
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Inicial grande en círculo con borde blanco
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.08f))
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = league.name.take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = league.name,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Chip de estado "ACTIVA"
                        Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.22f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFF69F0AE), CircleShape))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "ACTIVA",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // ===== ZONA INFERIOR =====
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                // Descripción
                if (league.description.isNotEmpty()) {
                    Text(
                        text = league.description,
                        fontSize = 13.sp,
                        color = textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.12f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Botón Editar
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Editar", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        
                        // Botón Eliminar
                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red.copy(alpha = 0.7f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Botón Administrar con el color de acento de la liga
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text("Administrar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// =====================================================================
//  LeagueFormDialog
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueFormDialog(
    initialLeague: League? = null,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialLeague?.name ?: "") }
    var desc by remember { mutableStateOf(initialLeague?.description ?: "") }

    val bgColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = bgColor,
        titleContentColor = FulbitoGreen,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(FulbitoGreen.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (initialLeague == null) "Crear Nueva Liga" else "Editar Liga",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Liga") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FulbitoGreen,
                        focusedLabelColor = FulbitoGreen,
                        cursorColor = FulbitoGreen,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FulbitoGreen,
                        focusedLabelColor = FulbitoGreen,
                        cursorColor = FulbitoGreen,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, desc) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FulbitoGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FulbitoGreen),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }
    )
}

