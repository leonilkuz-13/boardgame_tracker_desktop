package player

import board.Board

class _Player(override val name: String, override val myBoard : Board, override val enemyBoard: Board): Player {
    override var radarCharges = 2
    override var bomberCharges = 1

    class ItemDepletedException(message: String) : RuntimeException(message)

    private fun hasRadar(): Boolean {
        return radarCharges > 0
    }

    private fun hasBomber(): Boolean {
        return bomberCharges > 0
    }

    override fun isUseRadar(): Unit {
        if (!hasRadar()) {
            throw ItemDepletedException("you don't have radars")
        }
        radarCharges--
    }

    override fun isUseBomber(): Unit {
        if (!hasBomber()) {
            throw ItemDepletedException("you don't have bomber")
        }
        bomberCharges--
    }
}