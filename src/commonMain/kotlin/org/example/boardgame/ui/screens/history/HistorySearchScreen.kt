package org.example.boardgame.ui.screens.history

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
fun HistorySearchScreen(
    viewModel: HistorySearchViewModel,
    onMatchFound: (Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(viewModel.isSearchSuccessful) {
        if (viewModel.isSearchSuccessful) {
            onMatchFound(viewModel.matchId.toInt())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PaperWhite),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f).padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, PencilDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Match History Search",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PencilDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = viewModel.matchId,
                    onValueChange = { viewModel.matchId = it },
                    label = { Text("Enter Match ID") },
                    isError = viewModel.error != null,
                    supportingText = { viewModel.error?.let { Text(it) } },
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
                        text = "Replay Match",
                        onClick = { viewModel.onSearch() },
                        modifier = Modifier.weight(1f),
                        backgroundColor = PencilGreen,
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}
