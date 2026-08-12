// Pantalla de: AdminMatchScreen
package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.data.remote.Match
import mx.utng.cfga.fulbitoapp.data.remote.Team
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.foundation.clickable



@Composable
fun AdminMatchScreen(navController: NavController, viewModel: AdminViewModel) {
    val matches by viewModel.matches.collectAsState()
    val teams by viewModel.teams.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<Match?>(null) }

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Partidos", fontWeight = FontWeight.Bold, color = FulbitoGreen) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = FulbitoGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(
                    onClick = {
                        matchToEdit = null
                        showForm = !showForm
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo Partido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (showForm) {
                item {
                    MatchFormCard(
                        teams = teams,
                        initialMatch = matchToEdit,
                        onCancel = { showForm = false },
                        onRegister = { id, home, away, homeId, awayId, fecha, hora, cancha ->
                            if (id != null) {
                                viewModel.updateMatch(
                                    id,
                                    Match(
                                        homeTeam = home,
                                        awayTeam = away,
                                        homeTeamRef = homeId,
                                        awayTeamRef = awayId,
                                        fecha = fecha,
                                        hora = hora,
                                        cancha = cancha
                                    )
                                )
                            } else {
                                viewModel.createMatch(
                                    Match(
                                        homeTeam = home,
                                        awayTeam = away,
                                        homeTeamRef = homeId,
                                        awayTeamRef = awayId,
                                        fecha = fecha,
                                        hora = hora,
                                        cancha = cancha
                                    )
                                )
                            }
                            showForm = false
                        }
                    )
                }
            }

            items(matches) { match ->
                MatchCard(
                    match = match,
                    onEdit = {
                        matchToEdit = match
                        showForm = true
                    },
                    onDelete = { viewModel.deleteMatch(match._id ?: "") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormCard(
    teams: List<Team>,
    initialMatch: Match?,
    onCancel: () -> Unit,
    onRegister: (String?, String, String, String?, String?, String, String, String) -> Unit
) {
    var fecha by remember(initialMatch) { mutableStateOf(initialMatch?.fecha ?: "") }
    var hora by remember(initialMatch) { mutableStateOf(initialMatch?.hora ?: "") }
    var cancha by remember(initialMatch) { mutableStateOf(initialMatch?.cancha ?: "") }
    var homeTeam by remember(initialMatch, teams) { mutableStateOf(teams.find { it._id == initialMatch?.homeTeamRef }) }
    var awayTeam by remember(initialMatch, teams) { mutableStateOf(teams.find { it._id == initialMatch?.awayTeamRef }) }
    var expandedHome by remember { mutableStateOf(false) }
    var expandedAway by remember { mutableStateOf(false) }

    val title = if (initialMatch == null) "Crear Partido" else "Editar Partido"
    val btnText = if (initialMatch == null) "Crear" else "Guardar"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            val context = LocalContext.current
            val calendar = Calendar.getInstance()

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("FECHA", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { },
                        placeholder = { Text("dd/mm/aaaa") },
                        singleLine = true,
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray
                        )
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("HORA", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = hora,
                        onValueChange = { },
                        placeholder = { Text("HH:mm") },
                        singleLine = true,
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.clickable {
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    hora = String.format("%02d:%02d", hourOfDay, minute)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray
                        )
                    )
                }
            }

            Text("CANCHA", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
            OutlinedTextField(
                value = cancha,
                onValueChange = { cancha = it },
                placeholder = { Text("Ubicación de la cancha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FulbitoGreen,
                    focusedLabelColor = FulbitoGreen,
                    cursorColor = FulbitoGreen,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("EQUIPO LOCAL", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    ExposedDropdownMenuBox(expanded = expandedHome, onExpandedChange = { expandedHome = it }) {
                        OutlinedTextField(
                            value = homeTeam?.name ?: "Seleccionar",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHome) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FulbitoGreen,
                                focusedTrailingIconColor = FulbitoGreen,
                                cursorColor = FulbitoGreen,
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )
                        ExposedDropdownMenu(expanded = expandedHome, onDismissRequest = { expandedHome = false }) {
                            teams.forEach { team ->
                                DropdownMenuItem(text = { Text(team.name) }, onClick = {
                                    homeTeam = team
                                    expandedHome = false
                                })
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("EQUIPO VISITANTE", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    ExposedDropdownMenuBox(expanded = expandedAway, onExpandedChange = { expandedAway = it }) {
                        OutlinedTextField(
                            value = awayTeam?.name ?: "Seleccionar",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAway) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FulbitoGreen,
                                focusedTrailingIconColor = FulbitoGreen,
                                cursorColor = FulbitoGreen,
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )
                        ExposedDropdownMenu(expanded = expandedAway, onDismissRequest = { expandedAway = false }) {
                            teams.forEach { team ->
                                DropdownMenuItem(text = { Text(team.name) }, onClick = {
                                    awayTeam = team
                                    expandedAway = false
                                })
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FulbitoGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FulbitoGreen)
                ) { Text("Cancelar") }
                Button(
                    onClick = {
                        onRegister(
                            initialMatch?._id,
                            homeTeam?.name ?: "",
                            awayTeam?.name ?: "",
                            homeTeam?._id,
                            awayTeam?._id,
                            fecha, hora, cancha
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = homeTeam != null && awayTeam != null,
                    colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                ) { Text(btnText) }
            }
        }
    }
}

@Composable
fun MatchCard(match: Match, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isFinished = match.isFinished == true
    val statusColor = if (isFinished) Color(0xFFB71C1C) else Color.White
    val statusBg = if (isFinished) Color(0xFFFFEBEE) else FulbitoLightGreen
    val statusText = if (isFinished) "FINAL" else (match.currentPeriod ?: "EN ESPERA")

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusBg
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = FulbitoGreen)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${match.homeTeam ?: ""} vs ${match.awayTeam ?: ""}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF1E272E)
            )
            if (isFinished) {
                Text(
                    "${match.homeScore ?: 0} - ${match.awayScore ?: 0}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = FulbitoGreen
                )
            }
            if (match.fecha?.isNotBlank() == true || match.hora?.isNotBlank() == true) {
                Text("${match.fecha ?: ""} - ${match.hora ?: ""}", color = Color.Gray, fontSize = 13.sp)
            }
            if (match.cancha?.isNotBlank() == true) {
                Text("Cancha: ${match.cancha}", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

