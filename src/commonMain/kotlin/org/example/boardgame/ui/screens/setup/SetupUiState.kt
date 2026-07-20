package org.example.boardgame.ui.setup

import battleship.Ship
import common.CellStatus
import common.ShipType

data class SetupUiState(
    val myBoard: List<List<CellStatus>> = emptyList(),
    val placedShips: List<Ship> = emptyList(),
    val remainingShips: Map<ShipType, Int> = emptyMap(),
    val selectedShipType: ShipType? = null,
    val isHorizontal: Boolean = true,
    val setupLogs: String = "Select a ship type and place it on the board",
    val isReadyToStart: Boolean = false,
    val currentPlayerIndex: Int = 1, // 1 for Player 1, 2 for Player 2
    val currentPlayerName: String = ""
)
