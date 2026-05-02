package GameManager

import Console.Console
import Repository.History
import Repository.PlayerStats
import Repository.Statistics
import board._Board
import game.Game
import common.Move
import common.MoveResult
import common.TurnOwner
import engine._EnemyBoardEngine
import engine._MyBoardEngine
import game._Game
import player._Player

class _GameManager(
    private val stat: Statistics,
    private val console: Console // подумаю, как это прокидывать обратно потом
): GameManager {
    private var currentGame: Game? = null
    private var player1Name: String? = null
    private var player2Name: String? = null

    object CreateMatch {
        fun createMatch(name1: String, name2: String) : Game {
            // Игрок 1
            val p1MyBoard = _Board()
            val p1EnemyBoard = _Board()
            val player1 = _Player(name1, p1MyBoard, p1EnemyBoard)

            // Игрок 2
            val p2MyBoard = _Board()
            val p2EnemyBoard = _Board()
            val player2 = _Player(name2, p2MyBoard, p2EnemyBoard)

            return _Game(
                player1 = player1,
                player2 = player2,
                myBoardEngine = _MyBoardEngine(), // Движки можно переиспользовать
                enemyBoardEngine = _EnemyBoardEngine()
            )
        }
    }

    override fun loginPlayer(playerName: String) {
        val cleanName = if (playerName.isBlank()) "Guest" else playerName.trim() // пока так, после подумаю над именем пользователя

        val profile = stat.getPlayerStats(cleanName)
        if (profile == null) {
            println("System: profile not found. Registering a new commander: $cleanName")
            stat.createPlayer(cleanName) // может понадобится проверка на создание игрока -- иначе выход и слезы
            this.currentPlayerName = cleanName
            println("System: Welcome to the fleet, $cleanName")
        } else {
            this.currentPlayerName = cleanName
            println("System: Welcome to the fleet, $cleanName")
        }
    }

    override fun StartMatch() {
        val name = currentPlayerName
        if (name == null) {
            println("System: Sign in first!")
            return
        }

        this.currentGame = CreateMatch.createMatch(name)
        println("System: Welcome to the game! Moving to the setup phase...")
    }

    private fun handleGameOver(game: Game) {
        val winner = game.getWinner()
        val isPlayerWin = (winner == TurnOwner.PLAYER)

        if (isPlayerWin) {
            println("victory, the enemy fleet was destroyed!")
        } else {
            println("defeat, your fleet went to the bottom")
        }

        val nameToSave = currentPlayerName
        if (nameToSave != null && nameToSave != "Guest") {
            stat.saveMatchResult(nameToSave, isPlayerWin)
        }

        currentGame = null
        println("Game over. Waiting for a new session...")
    }

    private fun processMoveResult(result: MoveResult, game: Game) {
        when (result) {
            is MoveResult.Error -> {
                println("System: ${result.reason}")
            }
            is MoveResult.Success.Miss,
            is MoveResult.Success.Sunk,
            is MoveResult.Success.Hit,
            is MoveResult.GrandResult,
            is MoveResult.ScanResult,
            is MoveResult.ShipInstall -> {
                println("move accepted: status = $result")
            }
            is MoveResult.Success.Over -> {
                handleGameOver(game)
            }
        }
    }

    override fun handleMove(action: Move) {
        val game = currentGame
        if (game == null) {
            println("System: the match hasn't started yet")
            return
        }

        val result = game.move(action)
        processMoveResult(result, game)
    }

    override fun getLeaderboard(): List<PlayerStats> {
        return stat.getTopPlayers(10)
    }

    override fun getPlayerProfile(name: String): PlayerStats? {
        return stat.getPlayerStats(name)
    }

    override fun getMatchHistory(): List<Pair<Move, MoveResult>> {
        val game = currentGame ?: return emptyList()
        return game.getHistory()
    }
}