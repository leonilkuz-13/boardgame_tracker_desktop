package battleship

import common.Coordinate

import common.SpecialShape

abstract class BaseShip(override val coordinates: List<Coordinate>) : Ship {
    override val shape: SpecialShape? = null
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