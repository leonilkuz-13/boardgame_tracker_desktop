package Attack

import game.Move
import board.Board
import common.MoveResult

interface Attack {
    fun handle(action: Move, board: Board) : MoveResult // сам удар
}