package GameManager

import Repository.History
import common.PlayerStats
import Repository.Statistics
import board.BoardImpl
import common.ManagerResult
import game.Game
import common.Move
import common.MoveResult
import common.TurnOwner
import engine.EnemyBoardEngineImpl
import engine.MyBoardEngineImpl
import game.GameImpl
import player.PlayerImpl

class GameManagerImpl(
    private val stat: Statistics,
    private val history: History
): GameManager {
    private var currentGame: Game? = null
    private var player1Name: String? = null
    private var player2Name: String? = null

    private fun createMatch(name1: String, name2: String): Game {
        val p1MyBoard = BoardImpl()
        val p1EnemyBoard = BoardImpl()
        val player1 = PlayerImpl(name1, p1MyBoard, p1EnemyBoard)

        val p2MyBoard = BoardImpl()
        val p2EnemyBoard = BoardImpl()
        val player2 = PlayerImpl(name2, p2MyBoard, p2EnemyBoard)

        return GameImpl(
            player1 = player1,
            player2 = player2,
            myBoardEngine = MyBoardEngineImpl(),
            enemyBoardEngine = EnemyBoardEngineImpl()
        )
    }

    // связь с репозиторием: создание игрока, если его не было
    override fun loginPlayer(playerName: String): ManagerResult {
        val cleanName = if (playerName.isBlank()) "Guest" else playerName.trim()

        val profile = stat.getPlayerStats(cleanName)
        if (profile == null) {
            stat.createPlayer(cleanName)
        }

        if (player1Name == null) {
            player1Name = cleanName
            return ManagerResult.Success
        } else if (player2Name == null) {
            player2Name = cleanName
            return ManagerResult.Success
        } else if (player1Name == cleanName || player2Name == cleanName) {
            return ManagerResult.Failure("Player $cleanName is already in the lobby")
        } else {
            return ManagerResult.Failure("There are no seats. The match is already taking place: $player1Name and $player2Name.")
        }
    }

    override fun startMatch(): ManagerResult {
        val name1 = player1Name
        val name2 = player2Name

        if (name1 == null || name2 == null) {
            return ManagerResult.Failure("Unable to start match. Both players must log in.")
        }

        if (currentGame != null) {
            return ManagerResult.Failure("The game has already started. Complete the current one before starting a new one.")
        }
        currentGame = createMatch(name1, name2)

        return ManagerResult.Success
    }

    // в Game объявлен список historyLog -- просто список ходов, который в оперативной памяти лежит. Когда игра закончена, этот список прокидывается к контроллеру и записывается в базу и состояние игры сбрасывается
    private fun handleGameOver(game: Game) {
        val winner = game.getWinner()
        val isPlayer1Win = (winner == TurnOwner.PLAYER)

        val player1 = player1Name ?: "Guest1"
        val player2 = player2Name ?: "Guest2"
        val winnerName = if (isPlayer1Win) player1 else player2

        if (player1 != "Guest1") stat.saveMatchResult(player1, isPlayer1Win)
        if (player2 != "Guest2") stat.saveMatchResult(player2, !isPlayer1Win)

        val historyLog = game.getHistory()
        history.saveMatch(player1, player2, winnerName, historyLog)

        abortMatch()
    }

    // Основная задача этого метода -- результату прервать игру, если она закончена
    private fun processMoveResult(result: MoveResult, game: Game) {
        when (result) {
            is MoveResult.Error -> {
                println("System Error: ${result.reason}")
            }
            is MoveResult.Success.Over -> {
                handleGameOver(game)
            }
            is MoveResult.GrandResult -> {
                println("Move recorded: Bomber Attack")
                val isGameOver = result.results.any { it is MoveResult.Success.Over }
                if (isGameOver) {
                    handleGameOver(game)
                }
            }
            else -> {
                println("Move recorded: status = ${result.javaClass.simpleName}")
            }
        }
    }

    // вот тут переход по действию
    override fun handleMove(action: Move): MoveResult {
        val game = currentGame ?: return MoveResult.Error.GameError("System: No active match to track. Start a match first.")

        val result = game.move(action)
        processMoveResult(result, game)
        return result
    }

    override fun getLeaderboard(): List<PlayerStats> {
        return stat.getTopPlayers(10)
    }

    override fun getPlayerProfile(name: String): PlayerStats? {
        return stat.getPlayerStats(name)
    }

    override fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>? {
        return history.getMatchReplay(id)
    }

    override fun startGame(): MoveResult.Error? {
        return currentGame?.startGame()
    }

    override fun getCurrentPlayerName(): String {
        val game = currentGame ?: return "No active match"
        val currentTurnOwner = game.getCurrentTurnOwner()
        return when (currentTurnOwner) {
            TurnOwner.PLAYER -> player1Name ?: "Guest 1"
            TurnOwner.OPPONENT -> player2Name ?: "Guest 2"
        }
    }

    override fun switchTurn() {
        currentGame?.switchTurn()
    }

    override fun abortMatch() {
        currentGame = null
        player1Name = null
        player2Name = null
    }
}

