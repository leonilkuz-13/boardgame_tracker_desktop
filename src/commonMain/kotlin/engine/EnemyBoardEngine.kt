package engine

import common.MoveResult
import board.Board
import common.Move

interface EnemyBoardEngine {
    fun process(action: Move, board: Board): MoveResult // переход в Attack или Scan
}