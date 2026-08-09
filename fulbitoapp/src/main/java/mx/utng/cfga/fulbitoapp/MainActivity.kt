package mx.utng.cfga.fulbitoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import mx.utng.cfga.fulbitoapp.data.remote.SocketManager
import mx.utng.cfga.fulbitoapp.presentation.navigation.AppNavigation
import mx.utng.cfga.fulbitoapp.util.FulbitoNotificationService

private val LightColors = lightColorScheme(
    background = Color(0xFFF5F7F6),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

class MainActivity : ComponentActivity() {

    // Launcher para solicitar permiso POST_NOTIFICATIONS (Android 13+)
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // Permiso concedido: registrar los listeners de Socket.io
                registerSocketListeners()
            }
            // Si fue denegado, la app funciona igual pero sin notificaciones
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Crear los canales de notificación (obligatorio antes de mostrar cualquier notif)
        FulbitoNotificationService.init(this)

        // 2. Conectar Socket.io al servidor
        SocketManager.connect()

        // 3. Solicitar permiso en Android 13+ o registrar listeners directamente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tenemos permiso
                    registerSocketListeners()
                }
                else -> {
                    // Pedirle permiso al usuario
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android < 13: sin permiso en runtime
            registerSocketListeners()
        }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            MaterialTheme(colorScheme = if (isDarkMode) DarkColors else LightColors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { isDarkMode = !isDarkMode }
                    )
                }
            }
        }
    }

    /**
     * Registra los callbacks de Socket.io que disparan las notificaciones.
     * Se llama DESPUÉS de confirmar que tenemos permiso.
     */
    private fun registerSocketListeners() {
        val context = applicationContext

        // Limpiar listeners anteriores para evitar duplicados
        SocketManager.clearListeners()

        // ── Listener 1: Goles y Tarjetas (notifica a jugadores) ────────────────
        SocketManager.onMatchEvent { data ->
            runOnUiThread {
                val type = data.optString("type", "")
                val dorsal = data.optString("playerDorsal", "?")

                when (type.uppercase()) {
                    "GOAL", "GOL" ->
                        FulbitoNotificationService.showGoalNotification(context, dorsal)

                    "YELLOW_CARD", "YELLOW", "AMARILLA", "TARJETA_AMARILLA" ->
                        FulbitoNotificationService.showYellowCardNotification(context, dorsal)

                    "RED_CARD", "RED", "ROJA", "TARJETA_ROJA" ->
                        FulbitoNotificationService.showRedCardNotification(context, dorsal)
                }
            }
        }

        // ── Listener 2: Partido Terminado (notifica al admin + refresca lista) ─
        SocketManager.onMatchFinished { data ->
            runOnUiThread {
                val homeTeam  = data.optString("homeTeam", "Local")
                val awayTeam  = data.optString("awayTeam", "Visitante")
                val homeScore = data.optInt("homeScore", 0)
                val awayScore = data.optInt("awayScore", 0)

                FulbitoNotificationService.showMatchFinishedNotification(
                    context, homeTeam, awayTeam, homeScore, awayScore
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desconectar el socket cuando se cierra la app completamente
        SocketManager.disconnect()
    }
}
