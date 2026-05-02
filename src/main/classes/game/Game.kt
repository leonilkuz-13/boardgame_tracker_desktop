package game

import common.Move
import common.MoveResult
import common.TurnOwner
import player.Player

interface Game {
    val player1: Player
    val player2: Player
    fun move(action: Move) : MoveResult // действие
    fun getWinner(): TurnOwner? // кто выиграл в партии
    fun getHistory(): List<Pair<Move, MoveResult>>
}
