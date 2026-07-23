package mx.utng.cfga.fulbitoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.utng.cfga.fulbitoapp.presentation.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDashboardScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val user by loginViewModel.user.collectAsState()

    Scaffold(
        containerColor = FulbitoScreenBg,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Dashboard Jugador", 
                        fontWeight = FontWeight.Bold,
                        color = FulbitoGreen
                    ) 
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulbitoScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(FulbitoGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = FulbitoGreen,
                    modifier = Modifier.size(50.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "¡Hola, ${user?.username ?: "Jugador"}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FulbitoTextDark
            )
            
            Text(
                text = "Bienvenido a tu panel de jugador",
                fontSize = 14.sp,
                color = FulbitoTextLight,
                modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clickable { navController.navigate("player_profile") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(FulbitoGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = FulbitoGreen, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mi Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FulbitoTextDark)
                        Text("Estadísticas y contraseña", fontSize = 13.sp, color = FulbitoTextLight)
                    }
                }
            }
        }
    }
}
