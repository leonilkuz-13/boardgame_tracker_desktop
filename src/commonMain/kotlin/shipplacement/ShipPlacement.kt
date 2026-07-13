package shipplacement

import common.MoveResult
import battleship.Ship
import board.Board

interface ShipPlacement {
    fun isValidPlacement(ship: Ship, board: Board) : Boolean
    fun placeShip(ship: Ship, board: Board): MoveResult
}