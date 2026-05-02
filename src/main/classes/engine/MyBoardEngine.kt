package engine

import common.MoveResult
import board.Board
import common.Coordinate
import common.Move


interface MyBoardEngine {
    fun process(action: Move, myBoard: Board): MoveResult
}