package Attack

import common.MoveResult
import battleship.Ship
import board.Board
import common.CellStatus
import common.Coordinate
import common.Move


class AttackImpl: Attack {
    private fun isCellUntouched(coordinate: Coordinate, board: Board): Boolean {
        val status = board.getCellStatus(coordinate)
        return status == CellStatus.EMPTY || status == CellStatus.SHIP || status == CellStatus.BORDER
    }

    // проверка на правильную атаку (непонятно почему, я не прокинул ее в тесты. Прошу прощения, но ломать ничего не хочу)
    private fun isValidAttack(action: Move, board: Board): Boolean {
        return when (action) {
            is Move.SingleAttack -> {
                board.isWithinBounds(action.coordinate) && isCellUntouched(action.coordinate, board)
            }
            is Move.GrandAttack -> {
                board.isWithinBounds(action.center)
            }
            else -> false
        }
    }

    override fun handle(action: Move, board: Board): MoveResult {
        return when (action) {
            is Move.SingleAttack -> {
                singleAttack(action.coordinate, board)
            }
            is Move.GrandAttack -> {
                bomberAttack(action.center, board)
            }
            else -> MoveResult.Error.InvalidMove("invalid move action")
        }
    }

    // получение координат вокруг потопленного корабля
    private fun getCoordinatesAroundSunkShip(ship: Ship, board: Board): Set<Coordinate> {
        val result = mutableSetOf<Coordinate>()

        for (coordinate in ship.coordinates) {
            val neighbors = coordinate.getNeighbors()
            for (neighbor in neighbors) {
                val status = board.getCellStatus(neighbor)
                if (status == CellStatus.BORDER) {
                    result.add(neighbor)
                }
            }
        }

        return result
    }

    // одиночный выстрел
    private fun singleAttack(coordinate: Coordinate, board: Board): MoveResult {
        val singleAttackMove = Move.SingleAttack(coordinate)
        val status = board.getCellStatus(coordinate)

        if (!isValidAttack(singleAttackMove, board)) {
            return MoveResult.Error.InvalidMove("wrong attack")
        }

        if (status == CellStatus.SHIP) {
            board.updateCellStatus(coordinate, CellStatus.HIT) // обновил на уровне доски
            val ship: Ship? = board.getShipAt(coordinate)
            ship?.receiveHit(coordinate) // на уровне кораблей
            if (ship?.isSunk() == true) {
                val neighborsSunkShip = getCoordinatesAroundSunkShip(ship, board)

                for (neighbor in neighborsSunkShip) {
                    board.updateCellStatus(neighbor, CellStatus.MISS)
                }

                if (!board.hasAliveShips()) {
                    return MoveResult.Success.Over(coordinate, neighborsSunkShip, ship.type)
                }

                return MoveResult.Success.Sunk(coordinate, neighborsSunkShip, ship.type)
            }
            return MoveResult.Success.Hit(coordinate)
        }

        board.updateCellStatus(coordinate, CellStatus.MISS)
        return MoveResult.Success.Miss(coordinate)
    }

    // массовый удар
    private fun bomberAttack(center: Coordinate, board: Board): MoveResult {
        if (!isValidAttack(Move.GrandAttack(center), board)) {
            return MoveResult.Error.InvalidMove("Center coordinate is out of bounds")
        }

        val targetCoordinates = mutableListOf<Coordinate>()
        for (dx in -2..3) {
            for (dy in -2..3) {
                val targetCoordinate = Coordinate(center.x + dx, center.y + dy)
                if (board.isWithinBounds(targetCoordinate)) {
                    targetCoordinates.add(targetCoordinate)
                }
            }
        }

        val stepResults = mutableListOf<MoveResult.Success>()
        for (coordinate in targetCoordinates) {
            if (!isCellUntouched(coordinate, board)) continue

            val singleResult = singleAttack(coordinate, board)
            when (singleResult) {
                is MoveResult.Success -> {
                    stepResults.add(singleResult)
                    if (singleResult is MoveResult.Success.Over) {
                        break
                    }
                }
                is MoveResult.Error -> continue
                else -> Unit
            }
        }

        if (stepResults.isEmpty()) {
            return MoveResult.Error.InvalidMove("All cells in the bomber area are already hit")
        }

        return MoveResult.GrandResult(stepResults)
    }
}
