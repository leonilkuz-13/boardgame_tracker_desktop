package Scan

import common.MoveResult
import board.Board
import common.Coordinate

interface Scan {
    fun isValidScan(center: Coordinate, enemyBoard: Board): Boolean
    fun scan(center: Coordinate, enemyBoard: Board): MoveResult
}