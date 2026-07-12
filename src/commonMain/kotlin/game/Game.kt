package game

import common.Move
import common.MoveResult
import common.TurnOwner
import player.Player

interface Game {
    val player1: Player
    val player2: Player
    fun move(action: Move) : MoveResult // действие
    fun getWinner(): TurnOwner? // победитель партии (создается тут - прокидывается в контроллер)
    fun getHistory(): List<Pair<Move, MoveResult>> // история партии (отдает контроллеру приватный список)
    fun getCurrentTurnOwner() : TurnOwner // текущий игрок
    fun startGame(): MoveResult.Error? // переключение state, прокидывается к контроллеру.
    fun switchTurn() // переключение игроков
}
