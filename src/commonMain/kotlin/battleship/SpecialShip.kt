package battleship

import common.ShipType
import common.Coordinate
import common.SpecialShape

class SpecialShip(
    coordinates: List<Coordinate>,
    override val shape: SpecialShape?
) : BaseShip(coordinates) {
    override val type = ShipType.SPECIAL
}
