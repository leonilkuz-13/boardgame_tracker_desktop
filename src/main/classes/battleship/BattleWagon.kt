package battleship

import common.ShipType
import common.Coordinate

class BattleWagon (coordinates: List<Coordinate>) : BaseShip(coordinates) {
    override val type = ShipType.BATTLE_WAGON
}