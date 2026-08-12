// Pantalla de: PlayerProfileScreen
package mx.utng.cfga.fulbitoapp.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel
import mx.utng.cfga.fulbitoapp.presentation.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    playerViewModel: PlayerViewModel
) {
    val user by loginViewModel.user.collectAsState()
    val stats by playerViewModel.stats.collectAsState()
    val isLoading by playerViewModel.isLoading.collectAsState()
    val error by playerViewModel.error.collectAsState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(user?.id) {
        user?.id?.let { playerViewModel.fetchMyStats(it) }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPass, newPass ->
                user?.id?.let {
                    playerViewModel.changePassword(
                        userId = it,
                        oldPass = oldPass.trim(),
                        newPass = newPass.trim(),
                        onSuccess = { 
                            showPasswordDialog = false 
                            Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMsg -> 
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.ExtraBold, color = FulbitoTextDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = FulbitoTextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FulbitoGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                
                // --- BOTÓN CAMBIAR CONTRASEÑA ---
                item {
                    Button(
                        onClick = { showPasswordDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E272E)), // Color oscuro elegante
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cambiar Contraseña", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }

                if (error != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = error!!, color = Color(0xFFD32F2F), modifier = Modifier.padding(16.dp))
                        }
                    }
                } else if (stats != null) {
                    
                    // --- RENDIMIENTO GLOBAL ---
                    item {
                        Text(
                            "Rendimiento Global",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = FulbitoTextDark
                        )
                    }

                    // --- GRÁFICA RADAR (PENTÁGONO) ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RadarChart(
                                    values = listOf(
                                        stats!!.matchesPlayed.toFloat() / 20f,  // Partidos (max 20)
                                        stats!!.totalGoals.toFloat() / 30f,     // Goles (max 30)
                                        1f - (stats!!.yellowCards.toFloat() / 10f).coerceAtMost(1f), // Disciplina Amarillas
                                        1f - (stats!!.redCards.toFloat() / 5f).coerceAtMost(1f),    // Disciplina Rojas
                                        ((stats!!.totalGoals.toFloat() / (stats!!.matchesPlayed.coerceAtLeast(1)).toFloat()) / 3f).coerceAtMost(1f) // Efectividad
                                    ),
                                    labels = listOf("Partidos", "Goles", "Disciplina", "Limpieza", "Efectividad"),
                                    modifier = Modifier.size(240.dp)
                                )
                            }
                        }
                    }
                    
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PremiumFlatStatCard(
                                        title = "Partidos",
                                        value = stats!!.matchesPlayed.toString(),
                                        bgColor = Color(0xFFE8F5E9),
                                        textColor = FulbitoGreen
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    PremiumFlatStatCard(
                                        title = "Goles",
                                        value = stats!!.totalGoals.toString(),
                                        bgColor = Color(0xFFE3F2FD),
                                        textColor = Color(0xFF1976D2)
                                    )
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    PremiumFlatStatCard(
                                        title = "Amarillas",
                                        value = stats!!.yellowCards.toString(),
                                        bgColor = Color(0xFFFFF8E1),
                                        textColor = Color(0xFFFBC02D)
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    PremiumFlatStatCard(
                                        title = "Rojas",
                                        value = stats!!.redCards.toString(),
                                        bgColor = Color(0xFFFFEBEE),
                                        textColor = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }

                    // --- HISTORIAL DE PARTIDOS ---
                    item {
                        Text(
                            "Historial de Partidos",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = FulbitoTextDark,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (stats!!.history.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Aún no tienes estadísticas.", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    items(stats!!.history) { history ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(44.dp).background(FulbitoGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("#${history.dorsal ?: "?"}", color = FulbitoGreen, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Partido ID", color = Color.Gray, fontSize = 12.sp)
                                        Text("${history.matchId?.takeLast(6)?.uppercase() ?: "DESC"}", fontWeight = FontWeight.Bold, color = FulbitoTextDark, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    FlatStatItem("⚽", history.goals.toString(), "Goles")
                                    FlatStatItem("🟨", history.yellowCards.toString(), "Amarillas")
                                    FlatStatItem("🟥", history.redCards.toString(), "Rojas")
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
fun PremiumFlatStatCard(title: String, value: String, bgColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 14.sp, color = textColor.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FlatStatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = FulbitoTextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── RADAR CHART (Pentágono de Rendimiento) ──────────────────────────────────

/**
 * Gráfica de tipo radar (spider/pentágono) dibujada con Canvas de Compose.
 * No requiere librerías externas.
 *
 * @param values  Lista de 5 valores entre 0f y 1f (ya normalizados al llamar).
 * @param labels  Etiquetas para cada vértice.
 */
@Composable
fun RadarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    radarColor: Color = FulbitoGreen,
    gridColor: Color = Color(0xFFE0E0E0),
    labelColor: Color = Color(0xFF747D8C)
) {
    require(values.size == labels.size) { "values y labels deben tener el mismo tamaño" }
    val n = values.size
    val gridLevels = 4

    // TextMeasurer: la API oficial de Compose para medir y dibujar texto en Canvas
    val textMeasurer = rememberTextMeasurer()
    val labelTextStyle = TextStyle(
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = labelColor
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = (size.minDimension / 2f) * 0.60f
        val labelRadius = (size.minDimension / 2f) * 0.88f
        val angleStep = (2 * Math.PI / n).toFloat()
        val startAngle = -Math.PI.toFloat() / 2f

        fun vertexAt(level: Float, i: Int): Offset {
            val angle = startAngle + i * angleStep
            return Offset(
                cx + level * radius * kotlin.math.cos(angle),
                cy + level * radius * kotlin.math.sin(angle)
            )
        }

        // 1. Polígonos de grilla
        for (level in 1..gridLevels) {
            val fraction = level.toFloat() / gridLevels
            val gridPath = Path()
            for (i in 0 until n) {
                val v = vertexAt(fraction, i)
                if (i == 0) gridPath.moveTo(v.x, v.y) else gridPath.lineTo(v.x, v.y)
            }
            gridPath.close()
            drawPath(
                path = gridPath,
                color = if (level == gridLevels) gridColor.copy(alpha = 0.6f) else gridColor.copy(alpha = 0.25f),
                style = Stroke(width = if (level == gridLevels) 1.5f else 1f)
            )
        }

        // 2. Líneas radiales
        for (i in 0 until n) {
            drawLine(
                color = gridColor,
                start = Offset(cx, cy),
                end = vertexAt(1f, i),
                strokeWidth = 1f
            )
        }

        // 3. Área de datos
        val dataPath = Path()
        for (i in 0 until n) {
            val v = vertexAt(values[i].coerceIn(0f, 1f), i)
            if (i == 0) dataPath.moveTo(v.x, v.y) else dataPath.lineTo(v.x, v.y)
        }
        dataPath.close()
        drawPath(path = dataPath, color = radarColor.copy(alpha = 0.18f))
        drawPath(path = dataPath, color = radarColor, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

        // 4. Puntos en vértices
        for (i in 0 until n) {
            val v = vertexAt(values[i].coerceIn(0f, 1f), i)
            drawCircle(color = radarColor, radius = 6f, center = v)
            drawCircle(color = Color.White, radius = 3f, center = v)
        }

        // 5. Etiquetas con TextMeasurer (sin canvas nativo)
        for (i in 0 until n) {
            val angle = startAngle + i * angleStep
            val lx = cx + labelRadius * kotlin.math.cos(angle)
            val ly = cy + labelRadius * kotlin.math.sin(angle)

            val measured = textMeasurer.measure(text = labels[i], style = labelTextStyle)
            val tw = measured.size.width
            val th = measured.size.height

            drawText(
                textLayoutResult = measured,
                topLeft = Offset(lx - tw / 2f, ly - th / 2f)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var showOld by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("Cambiar Contraseña", fontWeight = FontWeight.ExtraBold, color = FulbitoTextDark) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = { Text("Contraseña Actual") },
                    trailingIcon = {
                        IconButton(onClick = { showOld = !showOld }) {
                            Icon(if (showOld) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (showOld) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FulbitoGreen,
                        focusedLabelColor = FulbitoGreen,
                        unfocusedBorderColor = Color(0xFFEEEEEE)
                    )
                )
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("Nueva Contraseña") },
                    trailingIcon = {
                        IconButton(onClick = { showNew = !showNew }) {
                            Icon(if (showNew) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FulbitoGreen,
                        focusedLabelColor = FulbitoGreen,
                        unfocusedBorderColor = Color(0xFFEEEEEE)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(oldPass, newPass) },
                enabled = oldPass.isNotBlank() && newPass.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen, disabledContainerColor = Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, color = if (oldPass.isNotBlank() && newPass.isNotBlank()) Color.White else Color.Gray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    )
}

