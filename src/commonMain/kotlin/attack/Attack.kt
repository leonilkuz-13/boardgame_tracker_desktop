package attack

import board.Board
import common.Move
import common.MoveResult

interface Attack {
    fun handle(action: Move, board: Board) : MoveResult // сам удар
}