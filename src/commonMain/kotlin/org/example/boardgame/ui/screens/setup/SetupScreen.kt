package org.example.boardgame.ui.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import common.ShipType
import org.example.boardgame.ui.components.BattleGrid
import org.example.boardgame.ui.theme.*

@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    onNavigateToCombat: () -> Unit
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${uiState.currentPlayerName}, Setup Fleet",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PencilDark,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                BattleGrid(
                    getStatus = { coord ->
                        uiState.myBoard.getOrNull(coord.y - 1)?.getOrNull(coord.x - 'A') ?: CellStatus.EMPTY
                    },
                    onCellClick = { coord ->
                        viewModel.onCellClicked(coord.x - 'A', coord.y - 1)
                    },
                    ships = uiState.placedShips,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.onRotateShip() },
                            colors = ButtonDefaults.buttonColors(containerColor = PencilDark)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (uiState.isHorizontal) "Horizontal" else "Vertical")
                        }

                        Button(
                            onClick = {
                                if (viewModel.onNextPlayerClicked()) {
                                    onNavigateToCombat()
                                }
                            },
                            enabled = uiState.isReadyToStart,
                            colors = ButtonDefaults.buttonColors(containerColor = PencilGreen)
                        ) {
                            Text(if (uiState.currentPlayerIndex == 1) "Next Player" else "To Battle!", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Available Ships", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // CRITICAL FIX: Only show ships with remaining count > 0
                    val activeTypes = ShipType.entries.filter { (uiState.remainingShips[it] ?: 0) > 0 }
                    
                    items(activeTypes) { type ->
                        val count = uiState.remainingShips[type] ?: 0
                        ShipTypeItem(
                            type = type,
                            count = count,
                            isSelected = uiState.selectedShipType == type,
                            onClick = { viewModel.onShipTypeSelected(type) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShipTypeItem(
    type: ShipType,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(80.dp).fillMaxHeight().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PencilGreen.copy(alpha = 0.1f) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp, 
            if (isSelected) PencilGreen else PencilLight.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(type.name.take(5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Left: ", fontSize = 12.sp, color = PencilDark)
        }
    }
}
