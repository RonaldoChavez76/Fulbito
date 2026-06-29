package mx.utng.srcp.fulbito.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

@Composable
fun ConfirmationDialog(
    show: Boolean,
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (show) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.size(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                    ) {
                        Text("NO", fontSize = 10.sp)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.size(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))
                    ) {
                        Text("SÍ", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
