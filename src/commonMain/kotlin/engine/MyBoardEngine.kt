package engine

import common.MoveResult
import board.Board
import common.Move

interface MyBoardEngine {
    fun process(action: Move, myBoard: Board): MoveResult // переход по переданному действию от пользователя
}