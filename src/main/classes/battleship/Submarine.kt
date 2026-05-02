package battleship

import common.ShipType
import common.Coordinate

class Submarine (coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.SUBMARINE
}
