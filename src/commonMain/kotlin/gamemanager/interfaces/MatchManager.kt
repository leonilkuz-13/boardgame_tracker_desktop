package gamemanager.interfaces

import common.ManagerResult
import common.MoveResult
import common.Move
import common.SpecialShape

interface MatchManager {
    fun startMatch(): ManagerResult
    fun startGame(): MoveResult.Error?
    fun abortMatch()
    fun handleMove(action: Move): MoveResult
    fun switchTurn()
    fun setSpecialShip(playerName: String, shape: SpecialShape)
    fun getSpecialShip(playerName: String): SpecialShape?
    fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>?
}

