package boardgame_tracker_desktop.initial_interface_UI.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boardgame_tracker_desktop.initial_interface_UI.Components.PencilButton
import boardgame_tracker_desktop.initial_interface_UI.UITheme.*

@Composable
fun FirstPage(onContinue: () -> Unit) {
    SeaBattleTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sea",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PencilDark
                )
                Text(
                    text = "Battle",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PencilDark
                )
                Spacer(modifier = Modifier.height(48.dp))
                PencilButton(
                    text = "Continue",
                    onClick = onContinue
                )
            }
        }
    }
}
