package org.example.boardgame.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.boardgame.ui.components.PencilButton
import org.example.boardgame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PaperWhite),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f).padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, PencilDark)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Player Authentication",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PencilDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = uiState.player1Name,
                    onValueChange = { viewModel.onPlayer1NameChange(it) },
                    label = { Text("Player 1 Name") },
                    isError = uiState.player1Error != null,
                    supportingText = { uiState.player1Error?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PencilGreen,
                        unfocusedBorderColor = PencilLight
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.player2Name,
                    onValueChange = { viewModel.onPlayer2NameChange(it) },
                    label = { Text("Player 2 Name") },
                    isError = uiState.player2Error != null,
                    supportingText = { uiState.player2Error?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PencilGreen,
                        unfocusedBorderColor = PencilLight
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    PencilButton(
                        text = "Back",
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PencilButton(
                        text = "Start Game",
                        onClick = { viewModel.onStartGame() },
                        modifier = Modifier.weight(1f),
                        backgroundColor = PencilGreen,
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}
