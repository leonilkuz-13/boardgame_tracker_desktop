package Scan

import common.MoveResult
import board.Board
import common.CellStatus
import common.Coordinate

class ScanImpl: Scan {
    override fun isValidScan(center: Coordinate, enemyBoard: Board): Boolean {
        return enemyBoard.isWithinBounds(center)
    }

    override fun scan(center: Coordinate, enemyBoard: Board): MoveResult {
        if (!isValidScan(center, enemyBoard)) {
            return MoveResult.Error.GameError("Scanning center is beyond the field boundaries")
        }

        val scannedData = mutableMapOf<Coordinate, CellStatus>()
        for (dx in -2..2) {
            for (dy in -2..2) {
                val targetCoordinate = Coordinate(center.x + dx, center.y + dy)
                if (enemyBoard.isWithinBounds(targetCoordinate)) {
                    val actualStatus = enemyBoard.getCellStatus(targetCoordinate)
                    scannedData[targetCoordinate] = actualStatus
                }
            }
        }

        return MoveResult.ScanResult(scannedData)
    }
}