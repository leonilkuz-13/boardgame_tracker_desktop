package org.example.boardgame.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import common.CellStatus
import org.example.boardgame.ui.components.BattleGrid
import org.example.boardgame.ui.components.PencilButton
import org.example.boardgame.ui.theme.*

@Composable
fun MatchReplayScreen(
    viewModel: MatchReplayViewModel,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Text(
                "Match Replay (ID: ${viewModel.matchId})",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PencilDark
            )
            
            Text(
                "Step: ${viewModel.currentStep + 1}",
                fontSize = 18.sp,
                color = PencilDark.copy(alpha = 0.6f)
            )

            if (viewModel.isFinished) {
                Text(
                    "WINNER: ${viewModel.winnerName ?: "Unknown"}", 
                    color = PencilGreen, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOARDS ---
            // On desktop/tablet we might want Row, but for most screens Column is safer for "larger" boards.
            // However, the screenshot shows them side-by-side. 
            // To make them TRULY larger, we should probably stack them on mobile or use a HorizontalPager.
            // But I will keep Row and just increase size to fill width.
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Player 1 Board
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        viewModel.p1Name, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 14.sp,
                        color = PencilDark
                    )
                    BattleGrid(
                        getStatus = { coord -> 
                            viewModel.p1Board.getOrNull(coord.y - 1)?.getOrNull(coord.x - 'A') ?: CellStatus.EMPTY 
                        },
                        onCellClick = {},
                        ships = emptyList(),
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Player 2 Board
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        viewModel.p2Name, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 14.sp,
                        color = PencilDark
                    )
                    BattleGrid(
                        getStatus = { coord -> 
                            viewModel.p2Board.getOrNull(coord.y - 1)?.getOrNull(coord.x - 'A') ?: CellStatus.EMPTY 
                        },
                        onCellClick = {},
                        ships = emptyList(),
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONTROLS ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PencilButton(
                    text = "<< Prev",
                    onClick = { viewModel.prevStep() },
                    enabled = viewModel.currentStep >= 0,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                )
                
                PencilButton(
                    text = "Menu",
                    onClick = onBack,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                )
                
                PencilButton(
                    text = "Next >>",
                    onClick = { viewModel.nextStep() },
                    enabled = !viewModel.isFinished,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    backgroundColor = PencilGreen,
                    contentColor = Color.White
                )
            }
        }

        if (viewModel.isFinished) {
            ReplayResultOverlay(onClose = onBack)
        }
    }
}

@Composable
fun ReplayResultOverlay(onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, PencilDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Match Finished!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PencilDark
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Replay completed. You can go back to history search.",
                    fontSize = 16.sp,
                    color = PencilDark.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                PencilButton(
                    text = "Finish",
                    onClick = onClose,
                    backgroundColor = PencilGreen,
                    contentColor = Color.White
                )
            }
        }
    }
}
