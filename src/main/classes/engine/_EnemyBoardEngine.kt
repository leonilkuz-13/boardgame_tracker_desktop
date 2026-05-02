package engine

import Attack.Attack
import Attack._Attack
import common.MoveResult
import Scan.Scan
import Scan._Scan
import board.Board
import common.Move

class _EnemyBoardEngine : EnemyBoardEngine {
    private val attackHandler: Attack = _Attack()
    private val scanHandler: Scan = _Scan()
    override fun process(action: Move, board: Board): MoveResult {
        return when (action) {
            is Move.SingleAttack -> attackHandler.handle(action, board)
            is Move.GrandAttack -> attackHandler.handle(action, board)
            is Move.Radar -> scanHandler.scan(action.coordinates, board)
            else -> MoveResult.Error.GameError("Invalid command for the engine")
        }
    }
}
