package integrationtests.shootingintonowheretests

import board.BoardImpl
import common.Coordinate
import common.Move
import common.MoveResult
import common.TurnOwner
import engine.EnemyBoardEngineImpl
import engine.MyBoardEngineImpl
import game.GameImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import player.PlayerImpl

class ShootingIntoNowhereTests {

    private lateinit var game: GameImpl
    private lateinit var player1: PlayerImpl
    private lateinit var player2: PlayerImpl

    @BeforeEach
    fun setup() {
        player1 = PlayerImpl("Player 1", BoardImpl())
        player2 = PlayerImpl("Player 2", BoardImpl())
        game = GameImpl(player1, player2, MyBoardEngineImpl(), EnemyBoardEngineImpl())
    }

    @Test
    fun `shooting an already revealed cell returns error and does not consume turn`() {
        game.startGame()

        game.move(Move.SingleAttack(Coordinate('A', 1)))
        Assertions.assertEquals(TurnOwner.OPPONENT, game.getCurrentTurnOwner(), "Turn should switch to Player 2")

        game.move(Move.SingleAttack(Coordinate('B', 2)))
        Assertions.assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner(), "Turn should be back to Player 1")

        val invalidShotMove = Move.SingleAttack(Coordinate('A', 1))
        val invalidShotResult = game.move(invalidShotMove)

        Assertions.assertTrue(
            invalidShotResult is MoveResult.Error.InvalidMove,
            "Shooting the same cell twice should return InvalidMove error"
        )

        Assertions.assertEquals(
            TurnOwner.PLAYER,
            game.getCurrentTurnOwner(),
            "Player 1 should not lose their turn after an invalid move"
        )

        game.move(Move.SingleAttack(Coordinate('A', 2)))
        Assertions.assertEquals(
            TurnOwner.OPPONENT,
            game.getCurrentTurnOwner(),
            "Turn should finally switch to Player 2 after a valid miss"
        )
    }

    @Test
    fun `bomber at the extreme edge of the board does not crash and crops area correctly`() {
        game.startGame()

        val bomberMove = Move.GrandAttack(Coordinate('O', 15))
        val bomberResult = game.move(bomberMove)

        Assertions.assertTrue(
            bomberResult is MoveResult.GrandResult,
            "Bomber should return GrandResult without throwing exceptions"
        )

        val grandResult = bomberResult as MoveResult.GrandResult

        Assertions.assertEquals(
            9,
            grandResult.results.size,
            "Explosion should be cropped to exactly 9 valid cells at the corner"
        )

        Assertions.assertEquals(0, player1.bomberCharges, "Bomber charge should be consumed")
    }
}