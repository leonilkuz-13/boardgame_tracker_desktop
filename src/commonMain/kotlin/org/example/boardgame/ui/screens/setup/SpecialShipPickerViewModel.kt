package org.example.boardgame.ui.setup

import common.SpecialShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpecialShipPickerViewModel {
    private val _uiState = MutableStateFlow(SpecialShipPickerUiState())
    val uiState: StateFlow<SpecialShipPickerUiState> = _uiState.asStateFlow()

    fun onShapeSelected(shape: SpecialShape) {
        _uiState.update { it.copy(selectedShape = shape) }
    }
}
