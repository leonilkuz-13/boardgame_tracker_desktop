package battleship

import common.ShipType
import common.Coordinate

class SpecialShip (coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.SPECIAL
}