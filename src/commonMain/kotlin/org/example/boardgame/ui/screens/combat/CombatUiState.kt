package org.example.boardgame.ui.screens.combat

import common.CellStatus
import battleship.Ship

data class CombatUiState(
    val p1RealBoard: List<List<CellStatus>> = emptyList(),
    val p2RealBoard: List<List<CellStatus>> = emptyList(),
    val p1FogBoard: List<List<CellStatus>> = emptyList(),
    val p2FogBoard: List<List<CellStatus>> = emptyList(),
    
    val p1Ships: List<Ship> = emptyList(),
    val p2Ships: List<Ship> = emptyList(),
    
    val currentPlayerName: String = "",
    val currentPlayerIndex: Int = 1,
    
    val p1RadarCharges: Int = 0,
    val p1BomberCharges: Int = 0,
    val p2RadarCharges: Int = 0,
    val p2BomberCharges: Int = 0,
    
    val battleLogs: List<String> = emptyList(),
    val isGameOver: Boolean = false,
    val winnerName: String? = null
)
