package engine

import ShipPlacement.ShipPlacement
import ShipPlacement.ShipPlacementImpl
import board.Board
import common.MoveResult
import common.Move


// передать Move.Install в action ????
class MyBoardEngineImpl: MyBoardEngine {
    private val installHandler: ShipPlacement = ShipPlacementImpl()

    override fun process(action: Move, myBoard: Board): MoveResult {
        return when (action) {
            is Move.Install -> installHandler.placeShip(action.ship, myBoard)
            else -> MoveResult.Error.GameError("Invalid command for the engine")
        }
    }
}

