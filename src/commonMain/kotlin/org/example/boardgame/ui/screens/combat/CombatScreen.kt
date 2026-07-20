package org.example.boardgame.ui.screens.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import battleship.Ship
import common.CellStatus
import org.example.boardgame.ui.components.BattleGrid
import org.example.boardgame.ui.theme.*

@Composable
fun CombatScreen(
    viewModel: CombatViewModel,
    onBackToMenu: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SeaBattleTheme {
        Scaffold(
            containerColor = PaperWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Admin Header
                Text(
                    text = if (uiState.isGameOver) "GAME OVER" else "ADMIN TERMINAL (2v2)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PencilDark,
                    fontWeight = FontWeight.ExtraBold
                )

                if (uiState.isGameOver) {
                    Text("Winner: ${uiState.winnerName}", color = PencilGreen, fontWeight = FontWeight.Bold)
                    Button(onClick = onBackToMenu) { Text("Exit") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PlayerQuickStats("P1", uiState.p1RadarCharges, uiState.p1BomberCharges)
                    Surface(color = PencilDark, shape = RoundedCornerShape(8.dp)) {
                        Text("NEXT: ${uiState.currentPlayerName}", color = Color.White, modifier = Modifier.padding(4.dp), fontSize = 10.sp)
                    }
                    PlayerQuickStats("P2", uiState.p2RadarCharges, uiState.p2BomberCharges)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2x2 Grid
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BoardWithLabel("P1 FLEET (REAL)", uiState.p1RealBoard, uiState.p1Ships, Modifier.weight(1f))
                        BoardWithLabel("P2 FLEET (REAL)", uiState.p2RealBoard, uiState.p2Ships, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BoardWithLabel(
                            label = "P1 RADAR VIEW", 
                            grid = uiState.p1FogBoard, 
                            ships = emptyList(), 
                            modifier = Modifier.weight(1f),
                            highlight = uiState.currentPlayerIndex == 1
                        ) { c -> viewModel.onCellClicked(1, c.x - 'A', c.y - 1) }
                        
                        BoardWithLabel(
                            label = "P2 RADAR VIEW", 
                            grid = uiState.p2FogBoard, 
                            ships = emptyList(), 
                            modifier = Modifier.weight(1f),
                            highlight = uiState.currentPlayerIndex == 2
                        ) { c -> viewModel.onCellClicked(2, c.x - 'A', c.y - 1) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerQuickStats(name: String, radar: Int, bomber: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("R: $radar | B: $bomber", fontSize = 10.sp, color = PencilLight)
    }
}

@Composable
private fun BoardWithLabel(
    label: String,
    grid: List<List<CellStatus>>,
    ships: List<Ship>,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    onCellClick: (common.Coordinate) -> Unit = {}
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (highlight) PenBlue else PencilDark)
        BattleGrid(
            getStatus = { coord -> 
                grid.getOrNull(coord.y - 1)?.getOrNull(coord.x - 'A') ?: CellStatus.EMPTY
            },
            onCellClick = onCellClick,
            ships = ships,
            modifier = Modifier.size(175.dp) // Large enough to see images
        )
    }
}
