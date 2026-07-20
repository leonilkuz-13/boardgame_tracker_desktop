package org.example.boardgame.ui.setup

import battleship.*
import gamemanager.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import common.Move
import common.MoveResult
import common.Coordinate
import common.ShipType
import common.SpecialShape
import common.resolveSpecialShape
import common.shapeOffsets

class SetupViewModel(
    private val gameManager: GameManager
) {
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        initSetupPhase(1)
    }

    private fun initSetupPhase(playerIndex: Int) {
        val counts = ShipType.entries.associateWith { it.maxCount }
        val name = if (playerIndex == 1) gameManager.getPlayer1Name() else gameManager.getPlayer2Name()
        
        _uiState.update {
            it.copy(
                myBoard = if (playerIndex == 1) gameManager.getPlayer1Board() else gameManager.getPlayer2Board(),
                placedShips = if (playerIndex == 1) gameManager.getPlayer1Ships() else gameManager.getPlayer2Ships(),
                remainingShips = counts,
                selectedShipType = null,
                isReadyToStart = false,
                currentPlayerIndex = playerIndex,
                currentPlayerName = name ?: "Player $playerIndex"
            )
        }
    }

    fun onShipTypeSelected(type: ShipType) {
        _uiState.update { it.copy(selectedShipType = type) }
    }

    fun onRotateShip() {
        _uiState.update { it.copy(isHorizontal = !it.isHorizontal) }
    }

    fun onCellClicked(x: Int, y: Int) {
        val state = _uiState.value
        val type = state.selectedShipType ?: return
        if ((state.remainingShips[type] ?: 0) <= 0) return

        val targetCoordinates = calculateCoordinates(x, y, type, state.isHorizontal)
        val ship: Ship = when (type) {
            ShipType.BATTLE_WAGON -> BattleWagon(targetCoordinates)
            ShipType.CARRIER -> Carrier(targetCoordinates)
            ShipType.CRUISER -> Cruiser(targetCoordinates)
            ShipType.DESTROYER -> Destroyer(targetCoordinates)
            ShipType.SUBMARINE -> Submarine(targetCoordinates)
            ShipType.SPECIAL -> {
                val playerName = if (state.currentPlayerIndex == 1) "Player 1" else "Player 2"
                val shape = gameManager.getSpecialShip(playerName) ?: SpecialShape.TOP_LEFT
                SpecialShip(targetCoordinates, shape)
            }
        }

        val result = gameManager.handleMove(Move.Install(ship, targetCoordinates))
        if (result is MoveResult.ShipInstall) {
            val newCounts = state.remainingShips.toMutableMap()
            newCounts[type] = (newCounts[type] ?: 1) - 1
            
            _uiState.update { 
                it.copy(
                    remainingShips = newCounts,
                    placedShips = if (state.currentPlayerIndex == 1) gameManager.getPlayer1Ships() else gameManager.getPlayer2Ships(),
                    myBoard = if (state.currentPlayerIndex == 1) gameManager.getPlayer1Board() else gameManager.getPlayer2Board(),
                    isReadyToStart = newCounts.values.all { count -> count == 0 }
                )
            }
        }
    }

    fun onNextPlayerClicked(): Boolean {
        if (_uiState.value.currentPlayerIndex == 1) {
            gameManager.switchTurn()
            initSetupPhase(2)
            return false 
        } else {
            return gameManager.startGame() == null
        }
    }

    private fun calculateCoordinates(startX: Int, startY: Int, type: ShipType, isHorizontal: Boolean): List<Coordinate> {
        val startChar = ('A'.code + startX).toChar()
        val startYInt = startY + 1
        
        if (type == ShipType.SPECIAL) {
            val shape = gameManager.getSpecialShip(_uiState.value.currentPlayerName) ?: SpecialShape.TOP_LEFT
            val offsets = shapeOffsets[shape] ?: emptyList()
            return offsets.map { (dx, dy) -> Coordinate((startChar.code + dx).toChar(), startYInt + dy) }
        }

        return (0 until type.size).map { offset ->
            if (isHorizontal) Coordinate((startChar.code + offset).toChar(), startYInt)
            else Coordinate(startChar, startYInt + offset)
        }
    }
}
