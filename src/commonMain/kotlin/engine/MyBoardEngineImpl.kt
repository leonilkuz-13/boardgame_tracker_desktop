package engine

import shipplacement.ShipPlacement
import shipplacement.ShipPlacementImpl
import board.Board
import common.MoveResult
import common.Move


class MyBoardEngineImpl: MyBoardEngine {
    private val installHandler: ShipPlacement = ShipPlacementImpl()

    override fun process(action: Move, myBoard: Board): MoveResult {
        return when (action) {
            is Move.Install -> installHandler.placeShip(action.ship, myBoard)
            else -> MoveResult.Error.GameError("Invalid command for the engine")
        }
    }
}

