package mx.utng.cfga.fulbitoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mx.utng.cfga.fulbitoapp.presentation.navigation.AppNavigation

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
