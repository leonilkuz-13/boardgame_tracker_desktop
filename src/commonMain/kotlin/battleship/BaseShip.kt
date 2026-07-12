package battleship

import common.Coordinate

abstract class BaseShip(override val coordinates: List<Coordinate>) : Ship {
    private val hitCoordinates = mutableSetOf<Coordinate>()
    override fun receiveHit(coordinate: Coordinate) {
        if (coordinates.contains(coordinate)) {
            hitCoordinates.add(coordinate)
        }
    }
    override fun isSunk(): Boolean {
        return hitCoordinates.size == coordinates.size
    }
}