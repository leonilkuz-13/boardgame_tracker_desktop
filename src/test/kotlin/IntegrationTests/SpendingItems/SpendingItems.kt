package IntegrationTests.SpendingItems

import board.BoardImpl
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

class SpendingItemsTest {

    private lateinit var game: GameImpl
    private lateinit var player1: PlayerImpl
    private lateinit var player2: PlayerImpl

    @BeforeEach
    fun setup() {
        val p1MyBoard = BoardImpl()
        val p1EnemyBoard = BoardImpl()
        player1 = PlayerImpl("Player 1", p1MyBoard, p1EnemyBoard)

        val p2MyBoard = BoardImpl()
        val p2EnemyBoard = BoardImpl()
        player2 = PlayerImpl("Player 2", p2MyBoard, p2EnemyBoard)

        game = GameImpl(player1, player2, MyBoardEngineImpl(), EnemyBoardEngineImpl())
    }

    @Test
    fun `bomber flow with turn switch between players`() {
        game.startGame()

        assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner(), "Should be Player 1's turn")

        val p1Bomber1 = game.move(Move.GrandAttack(Coordinate('A', 1)))
        assertTrue(p1Bomber1 is MoveResult.GrandResult, "Player 1's 1st bomber should succeed")

        assertEquals(TurnOwner.OPPONENT, game.getCurrentTurnOwner())
        game.switchTurn()

        val p1Bomber2 = game.move(Move.GrandAttack(Coordinate('B', 2)))
        assertTrue(p1Bomber2 is MoveResult.Error.GameError)
        assertEquals("you don't have bomber", (p1Bomber2 as MoveResult.Error.GameError).reason)

        val p1Single = game.move(Move.SingleAttack(Coordinate('J', 10)))
        assertTrue(p1Single is MoveResult.Success.Miss, "Player 1 single attack should be a Miss")


        assertEquals(TurnOwner.OPPONENT, game.getCurrentTurnOwner(), "Should be Player 2's turn now")

        val p2Bomber1 = game.move(Move.GrandAttack(Coordinate('A', 1)))
        assertTrue(p2Bomber1 is MoveResult.GrandResult, "Player 2's 1st bomber should succeed")

        game.switchTurn()

        val p2Bomber2 = game.move(Move.GrandAttack(Coordinate('E', 5)))
        assertTrue(p2Bomber2 is MoveResult.Error.GameError)
        assertEquals("you don't have bomber", (p2Bomber2 as MoveResult.Error.GameError).reason)

        val p2Single = game.move(Move.SingleAttack(Coordinate('M', 12)))
        assertTrue(p2Single is MoveResult.Success.Miss, "Player 2 single attack should be a Miss")

        assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner(), "Should return to Player 1")
    }

    @Test
    fun `radar flow with turn switch between players`() {
        game.startGame()

        val p1Radar1 = game.move(Move.Radar(Coordinate('A', 1)))
        assertTrue(p1Radar1 is MoveResult.ScanResult)

        val p1Radar2 = game.move(Move.Radar(Coordinate('B', 2)))
        assertTrue(p1Radar2 is MoveResult.ScanResult)

        val p1Radar3 = game.move(Move.Radar(Coordinate('C', 3)))
        assertTrue(p1Radar3 is MoveResult.Error.GameError)
        assertEquals("you don't have radars", (p1Radar3 as MoveResult.Error.GameError).reason)

        val p1Single = game.move(Move.SingleAttack(Coordinate('D', 4)))
        assertTrue(p1Single is MoveResult.Success.Miss)


        assertEquals(TurnOwner.OPPONENT, game.getCurrentTurnOwner(), "Should be Player 2's turn now")

        val p2Radar1 = game.move(Move.Radar(Coordinate('E', 5)))
        assertTrue(p2Radar1 is MoveResult.ScanResult)

        val p2Radar2 = game.move(Move.Radar(Coordinate('F', 6)))
        assertTrue(p2Radar2 is MoveResult.ScanResult)

        val p2Radar3 = game.move(Move.Radar(Coordinate('G', 7)))
        assertTrue(p2Radar3 is MoveResult.Error.GameError)
        assertEquals("you don't have radars", (p2Radar3 as MoveResult.Error.GameError).reason)

        val p2Single = game.move(Move.SingleAttack(Coordinate('H', 8)))
        assertTrue(p2Single is MoveResult.Success.Miss)

        assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner(), "Turn should be back to Player 1")
    }
}