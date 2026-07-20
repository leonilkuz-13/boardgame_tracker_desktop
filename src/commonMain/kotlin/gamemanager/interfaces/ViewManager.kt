package gamemanager.interfaces

import common.CellStatus
import battleship.Ship

interface ViewManager {
    fun getPlayer1Board(): List<List<CellStatus>>
    fun getPlayer2Board(): List<List<CellStatus>>
    fun getPlayer1ViewOfEnemy(): List<List<CellStatus>>
    fun getPlayer2ViewOfEnemy(): List<List<CellStatus>>
    fun getPlayer1Ships(): List<Ship>
    fun getPlayer2Ships(): List<Ship>
}
