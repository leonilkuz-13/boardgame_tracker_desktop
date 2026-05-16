package ShipPlacement

import common.MoveResult
import battleship.BattleWagon
import battleship.Carrier
import battleship.Cruiser
import battleship.Destroyer
import battleship.Ship
import battleship.SpecialShip
import battleship.Submarine
import board.Board
import common.CellStatus
import common.Coordinate

class ShipPlacementImpl: ShipPlacement {
    override fun isValidPlacement(ship: Ship, board: Board): Boolean {
        val maxAllowed = when (ship) {
            is BattleWagon -> 1
            is Carrier -> 1
            is Cruiser -> 3
            is Destroyer -> 4
            is Submarine -> 5
            is SpecialShip -> 1
            else -> 0
        }

        val currentShipsCount = board.getShips().count { it.type == ship.type }
        if (currentShipsCount >= maxAllowed) {
            return false
        }

        for (coordinate in ship.coordinates) {
            if (!board.isWithinBounds(coordinate)) {
                return false
            }
            if (board.getCellStatus(coordinate) != CellStatus.EMPTY) {
                return false
            }
        }
        return true
    }

    override fun placeShip(ship: Ship, board: Board): MoveResult {
        if (!isValidPlacement(ship, board)) {
            return MoveResult.Error.GameError("Invalid coordinates for ship placement")
        }

        val addedBorders = mutableSetOf<Coordinate>()

        for (coordinate in ship.coordinates) {
            board.updateCellStatus(coordinate, CellStatus.SHIP)
        }

        for (coordinate in ship.coordinates) {
            val neighbors = coordinate.getNeighbors()
            for (neighbor in neighbors) {
                if (board.isWithinBounds(neighbor) && board.getCellStatus(neighbor) == CellStatus.EMPTY) {
                    board.updateCellStatus(neighbor, CellStatus.BORDER)
                    addedBorders.add(neighbor)
                }
            }
        }

        board.addShip(ship)

        return MoveResult.ShipInstall(ship.coordinates, addedBorders, ship.type)
    }
}