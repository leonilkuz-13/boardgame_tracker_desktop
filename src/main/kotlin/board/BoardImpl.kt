package board

import common.CellStatus
import battleship.Ship
import common.Coordinate

class BoardImpl : Board {
    private val cells: MutableMap<Coordinate, CellStatus> = mutableMapOf()
    private val ships: MutableList<Ship> = mutableListOf()

    init {
        for (x in 'A'..'O') {
            for (y in 1 until 16) {
                cells[Coordinate(x, y)] = CellStatus.EMPTY
            }
        }
    }
    override fun addShip(ship: Ship) {
        ships.add(ship)
    }
    override fun getShips(): List<Ship> {
        return ships
    }
    override fun hasAliveShips(): Boolean {
        return ships.any { !it.isSunk() }
    }
    override fun getShipAt(coordinate: Coordinate): Ship? {
        return ships.find { ship ->
            ship.coordinates.any { it == coordinate }
        }
    }

    override fun getCellStatus(coordinate: Coordinate): CellStatus {
        return cells[coordinate] ?: throw IllegalStateException("out-of-field coordinate")
    }

    override fun updateCellStatus(coordinate: Coordinate, newStatus: CellStatus) {
        if (!cells.containsKey(coordinate)) {
            throw IllegalArgumentException("you can't update a cell out of field")
        }
        cells[coordinate] = newStatus
    }

    override fun hasShip(coordinate: Coordinate): Boolean {
        return cells[coordinate] == CellStatus.SHIP
    }

    override fun isWithinBounds(coordinate: Coordinate): Boolean {
        return coordinate.x in 'A'..'O' && coordinate.y in 1..15
    }
}