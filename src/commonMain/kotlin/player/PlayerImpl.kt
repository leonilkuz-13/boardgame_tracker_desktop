package player

import board.Board

class PlayerImpl(override val name: String, override val myBoard : Board): Player {
    override var radarCharges = 2
    override var bomberCharges = 1

    class ItemDepletedException(message: String) : RuntimeException(message)

    private fun hasRadar(): Boolean {
        return radarCharges > 0
    }

    private fun hasBomber(): Boolean {
        return bomberCharges > 0
    }

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