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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext as CoilContext
import mx.utng.cfga.fulbitoapp.data.remote.Player
import mx.utng.cfga.fulbitoapp.data.remote.Team
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
fun AdminPlayerScreen(navController: NavController, viewModel: AdminViewModel) {
    val players by viewModel.players.collectAsState()
    val teams by viewModel.teams.collectAsState()
    
    // Estado para saber qué equipo estamos viendo. Si es null, mostramos la lista de equipos.
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    
    var showForm by remember { mutableStateOf(false) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }
    
    var showCredentialsDialog by remember { mutableStateOf(false) }
    var generatedUsername by remember { mutableStateOf("") }
    var generatedPassword by remember { mutableStateOf("") }
    var credentialsError by remember { mutableStateOf("") }

    if (showCredentialsDialog) {
        AlertDialog(
            onDismissRequest = { showCredentialsDialog = false },
            title = { Text(if (credentialsError.isNotEmpty()) "Error" else "Credenciales Generadas") },
            text = {
                if (credentialsError.isNotEmpty()) {
                    Text(credentialsError)
                } else {
                    Column {
                        Text("Entrega estos datos al jugador. Solo se mostrarán esta vez.", color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Usuario: $generatedUsername", fontWeight = FontWeight.Bold)
                        Text("Contraseña: $generatedPassword", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCredentialsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Text(
                        text = if (selectedTeam == null) "Jugadores" else "${selectedTeam?.name}", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = FulbitoGreen
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (selectedTeam != null) {
                            selectedTeam = null
                            showForm = false
                        } else {
                            navController.popBackStack() 
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = FulbitoGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        }
    ) { padding ->
        if (selectedTeam == null) {
            // VISTA 1: Lista de Equipos
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (teams.isEmpty()) {
                    item {
                        Text("No hay equipos registrados. Ve a la sección de Equipos para crear uno primero.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    }
                }
                items(teams) { team ->
                    TeamSelectionCard(team = team, onClick = { selectedTeam = team })
                }
            }
        } else {
            // VISTA 2: Jugadores del Equipo Seleccionado
            val teamPlayers = players.filter { it.teamRef == selectedTeam?._id }
            
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Button(
                        onClick = {
                            playerToEdit = null
                            showForm = !showForm
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir a ${selectedTeam?.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                if (showForm) {
                    item {
                        val context = LocalContext.current
                        PlayerFormCard(
                            initialPlayer = playerToEdit,
                            onCancel = { showForm = false },
                            viewModel = viewModel,
                            context = context,
                            onRegister = { id, name, dorsal, pos, photo ->
                                if (id != null) {
                                    viewModel.updatePlayer(id, Player(name = name, dorsal = dorsal, position = pos, photoUrl = photo, teamRef = selectedTeam?._id))
                                } else {
                                    viewModel.createPlayer(Player(name = name, dorsal = dorsal, position = pos, photoUrl = photo, teamRef = selectedTeam?._id))
                                }
                                showForm = false
                            }
                        )
                    }
                }

                if (teamPlayers.isEmpty() && !showForm) {
                    item {
                        Text("Este equipo no tiene jugadores registrados todavía.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    }
                }

                items(teamPlayers) { player ->
                    PlayerCard(
                        player = player,
                        onEdit = {
                            playerToEdit = player
                            showForm = true
                        },
                        onDelete = { viewModel.deletePlayer(player._id ?: "") },
                        onGenerateAccount = { playerId ->
                            viewModel.generateAccount(
                                playerId = playerId,
                                onSuccess = { user, pass ->
                                    credentialsError = ""
                                    generatedUsername = user
                                    generatedPassword = pass
                                    showCredentialsDialog = true
                                },
                                onError = { err ->
                                    credentialsError = err
                                    showCredentialsDialog = true
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TeamSelectionCard(team: Team, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (team.shieldUrl?.isNotBlank() == true) {
                AsyncImage(
                    model = team.shieldUrl,
                    contentDescription = "Escudo",
                    modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(FulbitoLightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if(team.name.isNotBlank()) team.name.take(1).uppercase() else "?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Toque para ver jugadores", color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver jugadores", tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFormCard(
    initialPlayer: Player?,
    onCancel: () -> Unit,
    viewModel: AdminViewModel,
    context: android.content.Context,
    onRegister: (String?, String, String, String, String?) -> Unit
) {
    var name by remember(initialPlayer) { mutableStateOf(initialPlayer?.name ?: "") }
    var dorsal by remember(initialPlayer) { mutableStateOf(initialPlayer?.dorsal ?: "") }
    var selectedPosition by remember(initialPlayer) { mutableStateOf(initialPlayer?.position ?: "Delantero") }
    var expandedPos by remember { mutableStateOf(false) }
    var photoUrl by remember(initialPlayer) { mutableStateOf(initialPlayer?.photoUrl ?: "") }
    var isUploading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                isUploading = true
                val url = viewModel.uploadImage(context, uri)
                if (url != null) photoUrl = url
                isUploading = false
            }
        }
    }

    val positions = listOf("Portero", "Defensa", "Mediocampista", "Delantero")
    val title = if (initialPlayer == null) "Registrar Jugador" else "Editar Jugador"
    val btnText = if (initialPlayer == null) "Registrar" else "Guardar"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(FulbitoLightGreen).clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else if (photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = "Añadir foto", tint = FulbitoGreen)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("NOMBRE COMPLETO", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nombre del jugador") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FulbitoGreen,
                            focusedLabelColor = FulbitoGreen,
                            cursorColor = FulbitoGreen,
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("DORSAL (#)", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = dorsal,
                        onValueChange = { dorsal = it },
                        placeholder = { Text("Nº") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FulbitoGreen,
                            cursorColor = FulbitoGreen,
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )
                }
                Column(modifier = Modifier.weight(2f)) {
                    Text("POSICIÓN", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
                    ExposedDropdownMenuBox(expanded = expandedPos, onExpandedChange = { expandedPos = it }) {
                        OutlinedTextField(
                            value = selectedPosition,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPos) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FulbitoGreen,
                                focusedTrailingIconColor = FulbitoGreen,
                                cursorColor = FulbitoGreen,
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
                        )
                        ExposedDropdownMenu(expanded = expandedPos, onDismissRequest = { expandedPos = false }) {
                            positions.forEach { pos ->
                                DropdownMenuItem(text = { Text(pos) }, onClick = {
                                    selectedPosition = pos
                                    expandedPos = false
                                })
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(
                    onClick = onCancel, 
                    modifier = Modifier.weight(1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FulbitoGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FulbitoGreen)
                ) { Text("Cancelar") }
                Button(
                    onClick = { onRegister(initialPlayer?._id, name, dorsal, selectedPosition, photoUrl) },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && dorsal.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                ) { Text(btnText) }
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player, onEdit: () -> Unit, onDelete: () -> Unit, onGenerateAccount: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (player.photoUrl?.isNotBlank() == true) {
                val ctx = CoilContext.current
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(player.photoUrl)
                        .crossfade(true)
                        .memoryCacheKey("player_${player._id}_${player.photoUrl}")
                        .build(),
                    contentDescription = "Foto",
                    modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(FulbitoLightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${player.dorsal}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(player.name ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Posición: ${player.position ?: ""}", color = Color.Gray, fontSize = 13.sp)
            }
            Row {
                IconButton(onClick = { onGenerateAccount(player._id ?: "") }) {
                    Icon(Icons.Default.Lock, contentDescription = "Generar Cuenta", tint = Color(0xFFFFB300))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = FulbitoGreen)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                }
            }
        }
    }
}
