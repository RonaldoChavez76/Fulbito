package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FULBITO",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            letterSpacing = 2.sp
        )
        Text(
            text = "Panel de Árbitro",
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )
        
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.primaryButtonColors(backgroundColor = Color(0xFF1B8C42)),
            modifier = Modifier.size(50.dp)
        ) {
            Canvas(modifier = Modifier.size(20.dp).offset(x = 2.dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path = path, color = Color.White)
            }
        }
    }
}
