package org.example.boardgame.ui.screens.GameResult

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.boardgame.ui.components.PencilButton
import org.example.boardgame.ui.theme.*

@Composable
fun GameResultScreen(
    viewModel: GameResultViewModel,
    onNavigateBack: () -> Unit,
    onWatchReplay: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SeaBattleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PaperWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = if (uiState.isMyVictory) "VICTORY" else "DEFEAT",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = if (uiState.isMyVictory) PencilGreen else PencilRed,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp)
                        .border(2.dp, PencilDark, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.winnerName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PencilDark
                        )
                        Text(
                            text = "is the winner!",
                            fontSize = 16.sp,
                            color = PencilLight
                        )
                        
                        if (uiState.totalTurns > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = GridLineColor
                            )
                            Text(
                                text = "Total Turns: ${uiState.totalTurns}",
                                fontSize = 14.sp,
                                color = PencilDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(
                    modifier = Modifier.width(240.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PencilButton(
                        text = "Back to Menu",
                        onClick = {
                            viewModel.onReturnToMenuClicked()
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    PencilButton(
                        text = "Watch Replay",
                        onClick = {
                            viewModel.onWatchReplayClicked()
                            onWatchReplay()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
