package game

import battleship.Submarine
import board.Board
import board.BoardImpl
import common.Coordinate
import common.Move
import common.MoveResult
import common.ShipType
import common.TurnOwner
import engine.EnemyBoardEngine
import engine.MyBoardEngine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import player.Player

class GameImplTest {

    private lateinit var game: GameImpl
    private lateinit var fakePlayer1: FakePlayer
    private lateinit var fakePlayer2: FakePlayer
    private lateinit var fakeMyEngine: FakeMyEngine
    private lateinit var fakeEnemyEngine: FakeEnemyEngine

    @BeforeEach
    fun setup() {
        val dummyEnemyBoard1 = BoardImpl()
        val dummyEnemyBoard2 = BoardImpl()

        fakePlayer1 = FakePlayer("Player 1", 1, 1)
        fakePlayer2 = FakePlayer("Player 2", 1, 1)

        fakeMyEngine = FakeMyEngine()
        fakeEnemyEngine = FakeEnemyEngine()

        game = GameImpl(fakePlayer1, fakePlayer2, fakeMyEngine, fakeEnemyEngine)
    }

    @Test
    fun `cannot attack during SETUP state`() {
        val attackMove = Move.SingleAttack(Coordinate('A', 1))
        val result = game.move(attackMove)

        assertTrue(result is MoveResult.Error.GameError, "Expected GameError when attacking in SETUP state")
        val error = result as MoveResult.Error.GameError

        assertEquals("the battle hasn't started yet", error.reason)
    }

    @Test
    fun `cannot install ships during COMBAT state`() {
        game.startGame()

        val coords = listOf(Coordinate('A', 1))
        val submarine = Submarine(coords)
        val installMove = Move.Install(ship = submarine, coordinates = coords)
        val result = game.move(installMove)

        assertTrue(result is MoveResult.Error.GameError, "Expected GameError when installing in COMBAT state")
        val error = result as MoveResult.Error.GameError

        assertEquals("not your move!", error.reason)
    }

    @Test
    fun `move returns GameError when out of bomber charges`() {
        game.startGame()

        // Включаем имитацию пустых зарядов
        fakePlayer1.simulateBomberEmpty = true

        val bomberMove = Move.GrandAttack(Coordinate('E', 5))
        val result = game.move(bomberMove)

        assertTrue(result is MoveResult.Error.GameError, "Expected GameError when out of charges")
        val error = result as MoveResult.Error.GameError

        assertEquals("Out of bomber charges!", error.reason)
    }

    @Test
    fun `turn switches to opponent on Miss`() {
        game.startGame()
        fakeEnemyEngine.nextResult = MoveResult.Success.Miss(Coordinate('B', 2))

        assertEquals(TurnOwner.PLAYER, game.getCurrentTurnOwner())

        val attackMove = Move.SingleAttack(Coordinate('B', 2))
        game.move(attackMove)

        assertEquals(TurnOwner.OPPONENT, game.getCurrentTurnOwner(), "Expected turn to switch on Miss")
    }

    @Test
    fun `game finishes and winner is set on Over result`() {
        game.startGame()
        fakeEnemyEngine.nextResult = MoveResult.Success.Over(Coordinate('C', 3), emptySet(), ShipType.SUBMARINE)

        val attackMove = Move.SingleAttack(Coordinate('C', 3))
        game.move(attackMove)

        assertEquals(TurnOwner.PLAYER, game.getWinner(), "Expected PLAYER to be the winner")

        val nextMove = Move.SingleAttack(Coordinate('A', 1))
        val nextResult = game.move(nextMove)
        assertTrue(nextResult is MoveResult.Error.GameError, "Expected GameError after game is FINISHED")
    }
}

// --- FAKES ---
class FakePlayer(
    override val name: String,
    override var radarCharges: Int,
    override var bomberCharges: Int,
) : Player {
    override val myBoard: Board = BoardImpl()

    var simulateBomberEmpty = false

    override fun useBomber(): Boolean {
        return !simulateBomberEmpty
    }

    override fun useRadar(): Boolean {
        return true
    }
}

class FakeMyEngine : MyBoardEngine {
    override fun process(action: Move, myBoard: Board): MoveResult {
        return MoveResult.Success.Hit(Coordinate('A', 1))
    }
}

class FakeEnemyEngine : EnemyBoardEngine {
    var nextResult: MoveResult = MoveResult.Success.Hit(Coordinate('A', 1))

    override fun process(action: Move, board: Board): MoveResult {
        return nextResult
    }
}