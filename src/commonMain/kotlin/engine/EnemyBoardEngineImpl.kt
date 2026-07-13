package engine

import attack.Attack
import attack.AttackImpl
import common.MoveResult
import scan.Scan
import scan.ScanImpl
import board.Board
import common.Move

class EnemyBoardEngineImpl : EnemyBoardEngine {
    private val attackHandler: Attack = AttackImpl()
    private val scanHandler: Scan = ScanImpl()

    override fun process(action: Move, board: Board): MoveResult {
        return when (action) {
            is Move.SingleAttack -> attackHandler.handle(action, board)
            is Move.GrandAttack -> attackHandler.handle(action, board)
            is Move.Radar -> scanHandler.scan(action.center, board)
            else -> MoveResult.Error.GameError("Invalid command for the engine")
        }
    }
}
