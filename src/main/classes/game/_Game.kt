package game

import common.Move
import common.MoveResult
import common.GameState
import common.TurnOwner
import player.Player
import engine.MyBoardEngine
import engine.EnemyBoardEngine

class _Game(
    override val player1: Player,
    override val player2: Player,
    private val myBoardEngine: MyBoardEngine,
    private val enemyBoardEngine: EnemyBoardEngine
) : Game {

    private var state: GameState = GameState.SETUP
    private var turn: TurnOwner = TurnOwner.PLAYER
    private var winner: TurnOwner? = null

    private val historyLog = mutableListOf<Pair<Move, MoveResult>>()

    override fun move(action: Move): MoveResult {
        if (state == GameState.FINISHED) {
            return MoveResult.Error.GameError("the game is already over!")
        }

        val attacker = if (turn == TurnOwner.PLAYER) player1 else player2
        val defender = if (turn == TurnOwner.PLAYER) player2 else player1

        val result = when (action) {
            is Move.Install -> {
                if (state != GameState.SETUP) {
                    return MoveResult.Error.GameError("not your move!")
                }
                myBoardEngine.process(action, attacker.myBoard)
            }

            is Move.SingleAttack, is Move.GrandAttack, is Move.Radar -> {
                if (state != GameState.COMBAT) {
                    return MoveResult.Error.GameError("the battle hasn't started yet")
                }

                enemyBoardEngine.process(action, defender.myBoard)

            }
            else -> {
                MoveResult.Error.GameError("unknown action")
            }
        }

        if (result !is MoveResult.Error) {
            historyLog.add(Pair(action, result))
            analyzeResult(result)
        }

        return result
    }

    private fun analyzeResult(result: MoveResult) {
        when (result) {
            is MoveResult.Success.Miss -> {
                turn = if (turn == TurnOwner.PLAYER) TurnOwner.OPPONENT else TurnOwner.PLAYER
            }
            is MoveResult.Success.Over -> {
                state = GameState.FINISHED
                winner = turn
            }

            else -> Unit
        }
    }

    override fun getWinner(): TurnOwner? {
        return winner
    }

    override fun getHistory(): List<Pair<Move, MoveResult>> {
        return historyLog.toList()
    }

}