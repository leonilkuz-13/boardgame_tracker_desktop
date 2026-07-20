package gamemanager

import gamemanager.interfaces.PlayerManager
import gamemanager.interfaces.MatchManager
import gamemanager.interfaces.ViewManager
import repository.History
import common.PlayerStats
import repository.Statistics
import board.BoardImpl
import board.Board
import common.ManagerResult
import game.Game
import common.Move
import common.MoveResult
import common.TurnOwner
import common.CellStatus
import common.Coordinate
import common.SpecialShape
import common.ShipType
import battleship.Ship
import engine.EnemyBoardEngineImpl
import engine.MyBoardEngineImpl
import game.GameImpl
import player.PlayerImpl
import repository.MatchSummary

class GameManagerImpl(
    private val stat: Statistics,
    private val history: History
): GameManager {
    private var currentGame: Game? = null
    private var player1Name: String? = null
    private var player2Name: String? = null
    private val selectedSpecialShips = mutableMapOf<String, SpecialShape>()

    private fun createMatch(name1: String, name2: String): Game {
        val p1MyBoard = BoardImpl()
        val player1 = PlayerImpl(name1, p1MyBoard)

        val p2MyBoard = BoardImpl()
        val player2 = PlayerImpl(name2, p2MyBoard)

        return GameImpl(
            player1 = player1,
            player2 = player2,
            myBoardEngine = MyBoardEngineImpl(),
            enemyBoardEngine = EnemyBoardEngineImpl()
        )
    }

    override fun loginPlayer(playerName: String): ManagerResult {
        val cleanName = if (playerName.isBlank()) "Guest" else playerName.trim()
        val profile = stat.getPlayerStats(cleanName)
        if (profile == null) {
            stat.createPlayer(cleanName)
        }
        if (player1Name == cleanName || player2Name == cleanName) {
            return ManagerResult.Failure("Player $cleanName is already in the lobby")
        }
        if (player1Name == null) {
            player1Name = cleanName
            return ManagerResult.Success
        } else if (player2Name == null) {
            player2Name = cleanName
            return ManagerResult.Success
        } else {
            return ManagerResult.Failure("There are no seats.")
        }
    }

    override fun startMatch(): ManagerResult {
        val name1 = player1Name ?: return ManagerResult.Failure("Player 1 not logged in")
        val name2 = player2Name ?: return ManagerResult.Failure("Player 2 not logged in")
        if (currentGame != null) return ManagerResult.Failure("Game already started")
        currentGame = createMatch(name1, name2)
        return ManagerResult.Success
    }

    private fun handleGameOver(game: Game) {
        val winner = game.getWinner()
        val isPlayer1Win = (winner == TurnOwner.PLAYER)
        val player1 = player1Name ?: "Guest1"
        val player2 = player2Name ?: "Guest2"
        val winnerName = if (isPlayer1Win) player1 else player2
        if (player1 != "Guest1") stat.saveMatchResult(player1, isPlayer1Win)
        if (player2 != "Guest2") stat.saveMatchResult(player2, !isPlayer1Win)
        history.saveMatch(player1, player2, winnerName, game.getHistory())
        abortMatch()
    }

    override fun handleMove(action: Move): MoveResult {
        val game = currentGame ?: return MoveResult.Error.GameError("No active match")
        val result = game.move(action)
        when (result) {
            is MoveResult.Success.Over -> handleGameOver(game)
            is MoveResult.GrandResult -> {
                if (result.results.any { it is MoveResult.Success.Over }) handleGameOver(game)
            }
            else -> {}
        }
        return result
    }

    override fun getLeaderboard(): List<PlayerStats> = stat.getTopPlayers(10)
    override fun getPlayerStats(name: String): PlayerStats? = stat.getPlayerStats(name)
    override fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>? = history.getMatchReplay(id)
    override fun getMatchSummary(matchId: Int): MatchSummary? = history.getMatchSummary(matchId)
    override fun startGame(): MoveResult.Error? = currentGame?.startGame()
    
    override fun getPlayer1Name(): String? = player1Name
    override fun getPlayer2Name(): String? = player2Name

    override fun getCurrentPlayerName(): String {
        val owner = currentGame?.getCurrentTurnOwner() ?: TurnOwner.PLAYER
        return when (owner) {
            TurnOwner.PLAYER -> player1Name ?: "Player 1"
            TurnOwner.OPPONENT -> player2Name ?: "Player 2"
        }
    }

    override fun switchTurn() { currentGame?.switchTurn() }
    override fun abortMatch() {
        currentGame = null
        player1Name = null
        player2Name = null
        selectedSpecialShips.clear()
    }

    override fun setSpecialShip(playerName: String, shape: SpecialShape) {
        selectedSpecialShips[playerName] = shape
    }

    override fun getSpecialShip(playerName: String): SpecialShape? {
        return selectedSpecialShips[playerName]
    }

    // Admin / View methods - FIXED: get ships from board
    override fun getPlayer1Board(): List<List<CellStatus>> = getStatusGrid(currentGame?.player1?.myBoard)
    override fun getPlayer2Board(): List<List<CellStatus>> = getStatusGrid(currentGame?.player2?.myBoard)

    override fun getPlayer1ViewOfEnemy(): List<List<CellStatus>> {
        val realEnemyBoard = currentGame?.player2?.myBoard ?: return emptyGrid()
        return getFogGrid(realEnemyBoard)
    }

    override fun getPlayer2ViewOfEnemy(): List<List<CellStatus>> {
        val realEnemyBoard = currentGame?.player1?.myBoard ?: return emptyGrid()
        return getFogGrid(realEnemyBoard)
    }

    override fun getPlayer1Ships(): List<Ship> = currentGame?.player1?.myBoard?.getShips() ?: emptyList()
    override fun getPlayer2Ships(): List<Ship> = currentGame?.player2?.myBoard?.getShips() ?: emptyList()

    private fun getFogGrid(board: Board): List<List<CellStatus>> {
        return (1..15).map { y ->
            ('A'..'O').map { x ->
                val status = board.getCellStatus(Coordinate(x, y))
                if (status == CellStatus.SHIP) CellStatus.EMPTY else status
            }
        }
    }

    private fun emptyGrid() = List(15) { List(15) { CellStatus.EMPTY } }

    override fun getMyBoardStatusGrid(): List<List<CellStatus>> = getPlayer1Board()
    override fun getEnemyBoardStatusGrid(): List<List<CellStatus>> = getPlayer1ViewOfEnemy()

    private fun getStatusGrid(board: Board?): List<List<CellStatus>> {
        if (board == null) return emptyGrid()
        return (1..15).map { y ->
            ('A'..'O').map { x ->
                board.getCellStatus(Coordinate(x, y))
            }
        }
    }
}
