package battleship

import common.ShipType
import common.Coordinate

class Destroyer (coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.DESTROYER
}