package battleship

import common.ShipType
import common.Coordinate

class Cruiser (coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.CRUISER
}