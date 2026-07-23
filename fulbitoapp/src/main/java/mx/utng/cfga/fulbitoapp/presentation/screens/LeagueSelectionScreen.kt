package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.data.remote.League
import mx.utng.cfga.fulbitoapp.presentation.AdminViewModel
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueSelectionScreen(
    navController: NavController,
    adminViewModel: AdminViewModel,
    loginViewModel: LoginViewModel
) {
    val leagues by adminViewModel.leagues.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var leagueToEdit by remember { mutableStateOf<League?>(null) }

    LaunchedEffect(Unit) {
        adminViewModel.fetchLeagues()
    }

    if (showCreateDialog || leagueToEdit != null) {
        LeagueFormDialog(
            initialLeague = leagueToEdit,
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

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Mis Ligas", fontWeight = FontWeight.Bold, color = FulbitoGreen) },
                actions = {
                    IconButton(onClick = {
                        loginViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = FulbitoGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Liga", tint = Color.White)
            }
        }
    ) { padding ->
        if (leagues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay ligas registradas. Crea una para comenzar.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leagues) { league ->
                    LeagueCard(
                        league = league,
                        onClick = {
                            adminViewModel.selectLeague(league)
                            navController.navigate("admin_dashboard")
                        },
                        onEdit = {
                            leagueToEdit = league
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LeagueCard(league: League, onClick: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(league.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = FulbitoGreen)
                if (league.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(league.description, color = Color.Gray, fontSize = 14.sp)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Liga", tint = FulbitoGreen)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueFormDialog(
    initialLeague: League? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialLeague?.name ?: "") }
    var desc by remember { mutableStateOf(initialLeague?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialLeague == null) "Crear Nueva Liga" else "Editar Liga") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Liga") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción (Opcional)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, desc) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
