package Scan

import common.MoveResult
import board.Board
import common.CellStatus
import common.Coordinate

class _Scan: Scan {
    override fun isValidScan(
        coordinates: List<Coordinate>,
        enemyBoard: Board
    ): Boolean {
        if (coordinates.isEmpty()) return false

        return coordinates.all {enemyBoard.isWithinBounds(it)} // пока all, потом помягче вариант придумаю как сделать в sealed классе.
    }

    override fun scan(coordinates: List<Coordinate>, enemyBoard: Board): MoveResult {
        if (!isValidScan(coordinates, enemyBoard)) {
            return MoveResult.Invalid("scanning area beyond the field boundaries")
        }

        val scannedData = mutableMapOf<Coordinate, CellStatus>()
        for (coordinate in coordinates) {
            val actualStatus = enemyBoard.getCellStatus(coordinate)
            scannedData[coordinate] = actualStatus
        }
        return MoveResult.ScanResult(scannedData)
    }
}