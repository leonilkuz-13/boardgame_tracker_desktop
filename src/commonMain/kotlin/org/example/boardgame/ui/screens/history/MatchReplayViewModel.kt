package org.example.boardgame.ui.screens.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import battleship.Ship
import common.*
import gamemanager.GameManager

class MatchReplayViewModel(val matchId: Int, private val gameManager: GameManager) {
    private val history = gameManager.getMatchHistory(matchId) ?: emptyList()
    
    var currentStep by mutableStateOf(-1)
    var p1Board by mutableStateOf(emptyGrid())
    var p2Board by mutableStateOf(emptyGrid())
    var p1Name by mutableStateOf("Player 1")
    var p2Name by mutableStateOf("Player 2")
    var winnerName by mutableStateOf<String?>(null)
    var isFinished by mutableStateOf(false)

    init {
        val summary = gameManager.getMatchSummary(matchId)
        if (summary != null) {
            p1Name = summary.player1Name
            p2Name = summary.player2Name
            winnerName = summary.winnerName
        }
        rebuildBoards()
    }

    fun nextStep() {
        if (currentStep < history.size - 1) {
            currentStep++
            rebuildBoards()
        }
    }

    fun prevStep() {
        if (currentStep >= 0) {
            currentStep--
            rebuildBoards()
        }
    }

    private fun rebuildBoards() {
        val newP1 = emptyGrid().map { it.toMutableList() }.toMutableList()
        val newP2 = emptyGrid().map { it.toMutableList() }.toMutableList()

        for (i in 0..currentStep) {
            val (move, result) = history[i]
            applyMoveToGrids(move, result, newP1, newP2, i % 2 == 0)
            
            if (result is MoveResult.Success.Over) {
                if (i == history.size - 1) {
                    isFinished = true
                }
            }
        }
        
        p1Board = newP1.map { it.toList() }
        p2Board = newP2.map { it.toList() }
    }

    private fun applyMoveToGrids(
        move: Move, 
        result: MoveResult, 
        p1: MutableList<MutableList<CellStatus>>, 
        p2: MutableList<MutableList<CellStatus>>,
        isPlayer1Turn: Boolean
    ) {
        val targetGrid = if (isPlayer1Turn) p2 else p1
        
        when (move) {
            is Move.SingleAttack -> {
                updateCell(targetGrid, move.coordinate, result)
            }
            is Move.Install -> {
                move.coordinates.forEach { updateCell(if (isPlayer1Turn) p1 else p2, it, result) }
            }
            is Move.Radar -> {
                if (result is MoveResult.ScanResult) {
                    result.info.forEach { (coord, status) -> updateCell(targetGrid, coord, status) }
                }
            }
            is Move.GrandAttack -> {
                if (result is MoveResult.GrandResult) {
                    result.results.zip(move.center.getNeighbors() + move.center).forEach { (res, coord) ->
                        updateCell(targetGrid, coord, res)
                    }
                }
            }
        }
    }

    private fun updateCell(grid: MutableList<MutableList<CellStatus>>, coord: Coordinate, result: Any) {
        val x = coord.x - 'A'
        val y = coord.y - 1
        if (y in 0..14 && x in 0..14) {
            val status = when (result) {
                is MoveResult.Success.Hit -> CellStatus.HIT
                is MoveResult.Success.Miss -> CellStatus.MISS
                is MoveResult.Success.Sunk -> CellStatus.HIT
                is MoveResult.Success.Over -> CellStatus.HIT
                is CellStatus -> result
                else -> grid[y][x]
            }
            grid[y][x] = status
        }
    }

    private fun emptyGrid() = List(15) { List(15) { CellStatus.EMPTY } }
}
