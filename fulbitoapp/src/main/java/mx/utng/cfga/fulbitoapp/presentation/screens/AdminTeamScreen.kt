package mx.utng.cfga.fulbitoapp.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.utng.cfga.fulbitoapp.data.remote.Team
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel


@Composable
fun AdminTeamScreen(navController: NavController, viewModel: AdminViewModel) {
    val teams by viewModel.teams.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var showExistingDialog by remember { mutableStateOf(false) }
    var teamToEdit by remember { mutableStateOf<Team?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTeams()
    }

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Equipos", fontWeight = FontWeight.Bold, color = FulbitoGreen) },
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            teamToEdit = null
                            showForm = !showForm
                            showExistingDialog = false
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            showExistingDialog = !showExistingDialog
                            showForm = false
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FulbitoGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FulbitoGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Existente", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            if (showForm) {
                item {
                    TeamFormCard(
                        initialTeam = teamToEdit,
                        onCancel = { showForm = false },
                        onRegister = { id, name, cat, cap, imageUri, existingShieldUrl ->
                            coroutineScope.launch {
                                var finalShieldUrl = existingShieldUrl
                                if (imageUri != null) {
                                    val uploadedUrl = viewModel.uploadImage(context, imageUri)
                                    if (uploadedUrl != null) {
                                        finalShieldUrl = uploadedUrl
                                    }
                                }

                                if (id != null) {
                                    viewModel.updateTeam(id, Team(name = name, category = cat, captain = cap, shieldUrl = finalShieldUrl))
                                } else {
                                    viewModel.createTeam(Team(name = name, category = cat, captain = cap, shieldUrl = finalShieldUrl))
                                }
                                showForm = false
                            }
                        }
                    )
                }
            }

            if (showExistingDialog) {
                item {
                    val availableTeams = allTeams.filter { t -> !t.leagues.contains(viewModel.currentLeague?._id) }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Selecciona un equipo existente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (availableTeams.isEmpty()) {
                                Text("No hay equipos disponibles para agregar.", color = Color.Gray)
                            } else {
                                availableTeams.forEach { t ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                viewModel.addTeamToLeague(t)
                                                showExistingDialog = false 
                                            }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(t.name, modifier = Modifier.weight(1f))
                                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = FulbitoGreen)
                                    }
                                    HorizontalDivider()
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { showExistingDialog = false }, modifier = Modifier.align(Alignment.End)) {
                                Text("Cerrar", color = Color.Red)
                            }
                        }
                    }
                }
            }

            items(teams) { team ->
                TeamCard(
                    team = team,
                    onEdit = {
                        teamToEdit = team
                        showForm = true
                    },
                    onDelete = { viewModel.deleteTeam(team._id ?: "") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormCard(
    initialTeam: Team?, 
    onCancel: () -> Unit, 
    onRegister: (String?, String, String, String, Uri?, String) -> Unit
) {
    var name by remember(initialTeam) { mutableStateOf(initialTeam?.name ?: "") }
    var category by remember(initialTeam) { mutableStateOf(initialTeam?.category ?: "Mayor") }
    var captain by remember(initialTeam) { mutableStateOf(initialTeam?.captain ?: "") }
    var existingShieldUrl by remember(initialTeam) { mutableStateOf(initialTeam?.shieldUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    val categories = listOf("Mayor", "Sub-20", "Sub-18", "Sub-15", "Femenil")
    val title = if (initialTeam == null) "Registrar Equipo" else "Editar Equipo"
    val btnText = if (initialTeam == null) "Registrar" else "Guardar"

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            // Selector de Imagen
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE))
                    .border(2.dp, FulbitoGreen, CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Escudo seleccionado",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (existingShieldUrl.isNotBlank()) {
                    AsyncImage(
                        model = existingShieldUrl,
                        contentDescription = "Escudo actual",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Seleccionar Logo", tint = Color.Gray, modifier = Modifier.size(40.dp))
                }
            }
            Text("Toca para añadir un escudo", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(8.dp))

            Text("NOMBRE DEL EQUIPO", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FulbitoGreen,
                    focusedLabelColor = FulbitoGreen,
                    cursorColor = FulbitoGreen,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Text("CATEGORÍA", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FulbitoGreen,
                        focusedTrailingIconColor = FulbitoGreen,
                        cursorColor = FulbitoGreen,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )
                ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = {
                            category = cat
                            expandedCategory = false
                        })
                    }
                }
            }

            Text("CAPITÁN", fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
            OutlinedTextField(
                value = captain,
                onValueChange = { captain = it },
                placeholder = { Text("Nombre del capitan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FulbitoGreen,
                    focusedLabelColor = FulbitoGreen,
                    cursorColor = FulbitoGreen,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(
                    onClick = onCancel, 
                    modifier = Modifier.weight(1f), 
                    enabled = !isUploading,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FulbitoGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FulbitoGreen)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        isUploading = true
                        onRegister(initialTeam?._id, name, category, captain, selectedImageUri, existingShieldUrl)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank() && !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = FulbitoGreen)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(btnText)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamCard(team: Team, onEdit: () -> Unit, onDelete: () -> Unit) {
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Categoría: ${team.category}", color = Color.Gray, fontSize = 13.sp)
                if (team.captain.isNotBlank()) Text("Capitán: ${team.captain}", color = Color.Gray, fontSize = 13.sp)
            }
            Row {
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
