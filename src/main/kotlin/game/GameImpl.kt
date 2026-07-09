package game

import common.Move
import common.MoveResult
import common.GameState
import common.TurnOwner
import player.Player
import engine.MyBoardEngine
import engine.EnemyBoardEngine

class GameImpl(
    override val player1: Player,
    override val player2: Player,
    private val myBoardEngine: MyBoardEngine,
    private val enemyBoardEngine: EnemyBoardEngine
) : Game {

    private var state: GameState = GameState.SETUP // состояние игры
    private var turn: TurnOwner = TurnOwner.PLAYER // флажки по текущему игроку
    private var winner: TurnOwner? = null // победитель (на протяжении всей игры null, обновляется в конце)
    private val historyLog = mutableListOf<Pair<Move, MoveResult>>()
    private var currentPlayer: Player = player1
    private var waitingPlayer: Player = player2

    override fun move(action: Move): MoveResult {
        if (state == GameState.FINISHED) {
            return MoveResult.Error.GameError("the game is already over!")
        }

        val attacker = currentPlayer
        val defender = waitingPlayer

        // исправление: вот тут я убрал ненужный try-catch -- неуместен
        if (action is Move.GrandAttack) {
            if (!attacker.useBomber()) {
                return MoveResult.Error.GameError("Out of bomber charges!")
            }
        } else if (action is Move.Radar) {
            if (!attacker.useRadar()) {
                return MoveResult.Error.GameError("Out of radar charges!")
            }
        }

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
                switchTurn()
            }
            is MoveResult.Success.Over -> {
                state = GameState.FINISHED
                winner = turn
            }
            is MoveResult.GrandResult -> {
                val isGameOver = result.results.any { it is MoveResult.Success.Over }
                if (isGameOver) {
                    state = GameState.FINISHED
                    winner = turn
                } else {
                    val isAllMisses = result.results.all { it is MoveResult.Success.Miss }
                    if (isAllMisses) {
                        switchTurn()
                    }
                }
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

    override fun getCurrentTurnOwner(): TurnOwner {
        return turn
    }

    override fun startGame(): MoveResult.Error? {
        if (state != GameState.SETUP) {
            return MoveResult.Error.GameError("cannot start battle from current state.")
        }

        state = GameState.COMBAT
        return null
    }

    override fun switchTurn() {
        val temp = currentPlayer
        currentPlayer = waitingPlayer
        waitingPlayer = temp

        turn = if (turn == TurnOwner.PLAYER) TurnOwner.OPPONENT else TurnOwner.PLAYER
    }
}