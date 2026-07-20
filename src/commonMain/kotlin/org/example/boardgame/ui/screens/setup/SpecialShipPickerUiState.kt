package org.example.boardgame.ui.setup

import common.SpecialShape

data class SpecialShipPickerUiState(
    val availableShapes: List<SpecialShape> = SpecialShape.entries,
    val selectedShape: SpecialShape? = null
)
