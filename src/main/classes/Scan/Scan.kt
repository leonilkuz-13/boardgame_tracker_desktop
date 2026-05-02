package Scan

import common.MoveResult
import board.Board
import common.Coordinate

interface Scan {
    fun isValidScan(coordinates: List<Coordinate>, enemyBoard: Board): Boolean
    fun scan(coordinates: List<Coordinate>, enemyBoard: Board) : MoveResult
}