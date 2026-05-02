package Attack

import common.MoveResult
import battleship.Ship
import board.Board
import common.CellStatus
import common.Coordinate
import common.Move


class _Attack: Attack {
    private fun isCellUntouched(coordinate: Coordinate, board: Board): Boolean {
        val status = board.getCellStatus(coordinate)
        return status == CellStatus.EMPTY || status == CellStatus.SHIP || status == CellStatus.BORDER
    }

    private fun isValidAttack(action: Move, board: Board): Boolean {
        return when (action) {
            is Move.SingleAttack -> {
                board.isWithinBounds(action.coordinate) && isCellUntouched(action.coordinate, board)
            }

            is Move.GrandAttack -> {
                action.coordinates.all { board.isWithinBounds(it) } && action.coordinates.any {
                    isCellUntouched(
                        it,
                        board
                    )
                }
            }

            else -> false
        }
    }

    override fun handle(action: Move, board: Board): MoveResult {
        return when (action) {
            is Move.SingleAttack -> {
                this.singleAttack(action.coordinate, board)
            }

            is Move.GrandAttack -> {
                this.bomberAttack(action.coordinates, board)
            }

            else -> MoveResult.Invalid("invalid move action")
        }
    }

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

    private fun singleAttack(coordinate: Coordinate, board: Board): MoveResult {
        val _singleAttack = Move.SingleAttack(coordinate)
        val status = board.getCellStatus(coordinate)

        if (!isValidAttack(_singleAttack, board)) {
            return MoveResult.Invalid("wrong attack")
        }

        if (status == CellStatus.SHIP) {
            board.updateCellStatus(coordinate, CellStatus.HIT) // обновил на уровне доски
            val _ship: Ship? = board.getShipAt(coordinate)
            _ship?.receiveHit(coordinate) // на уровне кораблей
            if (_ship?.isSunk() == true) {
                val neighborsSunkShip = getCoordinatesAroundSunkShip(_ship, board)

                for (neighbor in neighborsSunkShip) {
                    board.updateCellStatus(neighbor, CellStatus.MISS)
                }

                if (!board.hasAliveShips()) {
                    return MoveResult.Success.Over(coordinate, neighborsSunkShip) // мб меседж закинуть еще
                }

                return MoveResult.Success.Sunk(coordinate, neighborsSunkShip)
            }
            return MoveResult.Success.Hit(coordinate)
        }

        board.updateCellStatus(coordinate, CellStatus.MISS)
        return MoveResult.Success.Miss(coordinate)
    }

    // меняю клетки единичными выстрелами, а GUI само разберется, как отрисовать клетки
    private fun bomberAttack(coordinates: List<Coordinate>, board: Board): MoveResult {
        val bomberAction = Move.GrandAttack(coordinates)

        if (!isValidAttack(bomberAction, board)) {
            return MoveResult.Invalid("wrong bomber attack")
        }

        val stepResults = mutableListOf<MoveResult.Success>()

        for (coordinate in coordinates) {
            val singleResult = singleAttack(coordinate, board)

            when (singleResult) {
                is MoveResult.Success -> {
                    stepResults.add(singleResult)

                    if (singleResult is MoveResult.Success.Over) {
                        break
                    }
                }

                is MoveResult.Invalid -> {
                    continue
                }

                else -> Unit
            }
        }

        return MoveResult.GrandResult(stepResults)
    }
}
