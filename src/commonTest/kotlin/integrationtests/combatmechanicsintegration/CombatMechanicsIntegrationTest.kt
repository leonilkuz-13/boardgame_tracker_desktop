package integrationtests.combatmechanicsintegration

import battleship.BattleWagon
import board.BoardImpl
import common.CellStatus
import common.Coordinate
import common.Move
import common.MoveResult
import common.TurnOwner
import engine.EnemyBoardEngineImpl
import engine.MyBoardEngineImpl
import game.GameImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import player.PlayerImpl

class CombatMechanicsIntegrationTest {

    private lateinit var game: GameImpl
    private lateinit var board2: BoardImpl // Нам нужен прямой доступ к доске оппонента для проверок

    @BeforeEach
    fun setup() {
        val p1MyBoard = BoardImpl()
        val player1 = PlayerImpl("Player 1", p1MyBoard)

        board2 = BoardImpl()
        val player2 = PlayerImpl("Player 2", board2)

        game = GameImpl(player1, player2, MyBoardEngineImpl(), EnemyBoardEngineImpl())
    }

    @Test
    fun `sinking a ship automatically fills surrounding cells with MISS`() {
        val shipCoords = listOf(Coordinate('B', 2), Coordinate('C', 2))
        board2.addShip(BattleWagon(shipCoords))
        board2.updateCellStatus(Coordinate('B', 2), CellStatus.SHIP)
        board2.updateCellStatus(Coordinate('C', 2), CellStatus.SHIP)

        val neighbors = Coordinate('B', 2).getNeighbors() + Coordinate('C', 2).getNeighbors()
        neighbors.filter { board2.isWithinBounds(it) && !shipCoords.contains(it) }.forEach {
            board2.updateCellStatus(it, CellStatus.BORDER)
        }

        game.startGame()

        val hitResult = game.move(Move.SingleAttack(Coordinate('B', 2)))

        assertTrue(hitResult is MoveResult.Success.Hit, "First shot should be a HIT")
        assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner(), "Player 1 should keep the turn after a hit")

        assertEquals(CellStatus.BORDER, board2.getCellStatus(Coordinate('A', 2)), "Halo should not be filled on just a Hit")

        val sunkResult = game.move(Move.SingleAttack(Coordinate('C', 2)))

        assertTrue(sunkResult is MoveResult.Success.Over, "Second shot should be OVER (Sunk the last ship)")

        assertEquals(CellStatus.MISS, board2.getCellStatus(Coordinate('A', 2)), "Halo cell A2 should be updated to MISS")
        assertEquals(CellStatus.MISS, board2.getCellStatus(Coordinate('A', 1)), "Halo cell A1 should be updated to MISS")
        assertEquals(CellStatus.MISS, board2.getCellStatus(Coordinate('D', 3)), "Halo cell D3 should be updated to MISS")
        assertEquals(CellStatus.MISS, board2.getCellStatus(Coordinate('C', 1)), "Halo cell C1 should be updated to MISS")
    }
}