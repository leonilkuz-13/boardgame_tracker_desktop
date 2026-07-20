package player

import board.Board
import battleship.Ship

class PlayerImpl(override val name: String, override val myBoard : Board): Player {
    override var radarCharges = 2
    override var bomberCharges = 1
    override val ships = mutableListOf<Ship>()

    override fun useBomber(): Boolean {
        if (bomberCharges > 0) {
            bomberCharges--
            return true
        }
        return false
    }

    override fun useRadar(): Boolean {
        if (radarCharges > 0) {
            radarCharges--
            return true
        }
        return false
    }
}
