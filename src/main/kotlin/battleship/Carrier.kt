package battleship

import common.ShipType
import common.Coordinate

class Carrier(coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.CARRIER
}