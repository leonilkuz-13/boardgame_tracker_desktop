package org.example.boardgame.ui.screens.shipselection

import common.SpecialShape

data class ShipSelectionUiState(
    val currentPlayerName: String = "",
    val playerIndex: Int = 1,
    val selectedShape: SpecialShape? = null,
    val availableShapes: List<SpecialShape> = SpecialShape.entries,
    val isFinished: Boolean = false
)
