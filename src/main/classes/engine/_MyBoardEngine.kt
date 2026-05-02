package engine

import ShipPlacement.ShipPlacement
import board.Board
import common.MoveResult
import common.Move
import ShipPlacement._ShipPlacement


class _MyBoardEngine: MyBoardEngine {
    private val installHandler: ShipPlacement = _ShipPlacement()
    override fun process(action: Move, myBoard: Board): MoveResult {
        return when (action) {
            is Move.Install -> installHandler.placeShip(action.ship, myBoard)
            else -> MoveResult.Error.GameError("Invalid command for the engine")
        }
    }
}

