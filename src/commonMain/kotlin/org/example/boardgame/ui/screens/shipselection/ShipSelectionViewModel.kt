package org.example.boardgame.ui.screens.shipselection

import gamemanager.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import common.SpecialShape

class ShipSelectionViewModel(
    private val gameManager: GameManager
) {
    private val _uiState = MutableStateFlow(ShipSelectionUiState())
    val uiState: StateFlow<ShipSelectionUiState> = _uiState.asStateFlow()

    init {
        updateCurrentPlayer()
    }

    private fun updateCurrentPlayer() {
        val displayName = if (_uiState.value.playerIndex == 1) {
            gameManager.getPlayer1Name() ?: "Player 1"
        } else {
            gameManager.getPlayer2Name() ?: "Player 2"
        }

        _uiState.update { it.copy(currentPlayerName = displayName, selectedShape = null) }
    }

    fun onShapeSelected(shape: SpecialShape) {
        _uiState.update { it.copy(selectedShape = shape) }
    }

    fun onConfirmSelection() {
        val state = _uiState.value
        val shape = state.selectedShape ?: return
        
        val playerName = state.currentPlayerName
        gameManager.setSpecialShip(playerName, shape)

        if (state.playerIndex == 1) {
            _uiState.update { it.copy(playerIndex = 2) }
            updateCurrentPlayer()
        } else {
            _uiState.update { it.copy(isFinished = true) }
        }
    }
}
