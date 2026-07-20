package org.example.boardgame.ui.screens.combat

import common.CellStatus
import common.Coordinate
import common.Move
import common.MoveResult
import common.TurnOwner
import gamemanager.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CombatViewModel(
    private val gameManager: GameManager
) {
    private val _uiState = MutableStateFlow(CombatUiState())
    val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

    init {
        syncStateWithEngine()
    }

    fun onCellClicked(playerIndex: Int, x: Int, y: Int) {
        val state = _uiState.value
        if (state.isGameOver) return
        
        if (playerIndex != state.currentPlayerIndex) return

        val target = Coordinate(('A'.code + x).toChar(), y + 1)
        val action = Move.SingleAttack(target)
        val result = gameManager.handleMove(action)
        
        processMoveResult(result)
        syncStateWithEngine()
    }

    private fun processMoveResult(result: MoveResult) {
        when (result) {
            is MoveResult.Success.Over -> {
                _uiState.update { it.copy(isGameOver = true, winnerName = gameManager.getCurrentPlayerName()) }
            }
            else -> {}
        }
    }

    private fun syncStateWithEngine() {
        val name = gameManager.getCurrentPlayerName()
        val index = if (name.contains("1")) 1 else 2
        
        // Mock items for now as engine doesn't expose them easily via manager yet
        // In a real scenario, we'd add getRadarCharges(playerIndex) to GameManager
        
        _uiState.update { currentState ->
            currentState.copy(
                p1RealBoard = gameManager.getPlayer1Board(),
                p2RealBoard = gameManager.getPlayer2Board(),
                p1FogBoard = gameManager.getPlayer1ViewOfEnemy(),
                p2FogBoard = gameManager.getPlayer2ViewOfEnemy(),
                p1Ships = gameManager.getPlayer1Ships(),
                p2Ships = gameManager.getPlayer2Ships(),
                currentPlayerName = name,
                currentPlayerIndex = index,
                // These are usually 2 radars and 1 bomber at start
                p1RadarCharges = 2, 
                p1BomberCharges = 1,
                p2RadarCharges = 2,
                p2BomberCharges = 1
            )
        }
    }
}
