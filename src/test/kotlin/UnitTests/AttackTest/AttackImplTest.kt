package UnitTests.AttackTest

import Attack.AttackImpl
import battleship.Destroyer
import battleship.Submarine
import board.BoardImpl
import common.CellStatus
import common.Coordinate
import common.Move
import common.MoveResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AttackImplTest {

    private lateinit var attackTracker: AttackImpl
    private lateinit var board: BoardImpl

    @BeforeEach
    fun setup() {
        attackTracker = AttackImpl()
        board = BoardImpl()
    }

    @Test
    fun `single attack on empty cell results in Miss`() {
        val coordinate = Coordinate('A', 1)
        val move = Move.SingleAttack(coordinate)

        val result = attackTracker.handle(move, board)

        Assertions.assertTrue(result is MoveResult.Success.Miss, "Expected result to be Miss")
        Assertions.assertEquals(CellStatus.MISS, board.getCellStatus(coordinate))
    }

    @Test
    fun `single attack on ship results in Hit`() {
        val coord1 = Coordinate('A', 1)
        val coord2 = Coordinate('B', 1)
        val destroyer = Destroyer(listOf(coord1, coord2))

        board.addShip(destroyer)
        board.updateCellStatus(coord1, CellStatus.SHIP)
        board.updateCellStatus(coord2, CellStatus.SHIP)

        val move = Move.SingleAttack(coord1)

        val result = attackTracker.handle(move, board)

        Assertions.assertTrue(result is MoveResult.Success.Hit, "Expected result to be Hit")
        Assertions.assertEquals(CellStatus.HIT, board.getCellStatus(coord1))
        Assertions.assertEquals(CellStatus.SHIP, board.getCellStatus(coord2))
    }

    @Test
    fun `single attack sinking the last ship results in Over`() {
        val targetCoord = Coordinate('C', 3)
        val submarine = Submarine(listOf(targetCoord))

        board.addShip(submarine)
        board.updateCellStatus(targetCoord, CellStatus.SHIP)

        val move = Move.SingleAttack(targetCoord)

        val result = attackTracker.handle(move, board)

        Assertions.assertTrue(result is MoveResult.Success.Over, "Expected result to be Over")
        Assertions.assertTrue(submarine.isSunk(), "Submarine should be sunk")
    }

    @Test
    fun `attack on already hit cell returns Error InvalidMove`() {
        val coordinate = Coordinate('A', 1)
        board.updateCellStatus(coordinate, CellStatus.MISS)

        val move = Move.SingleAttack(coordinate)

        val result = attackTracker.handle(move, board)

        Assertions.assertTrue(result is MoveResult.Error.InvalidMove, "Expected an Error.InvalidMove")
    }

    @Test
    fun `bomber attack hits multiple cells and returns GrandResult`() {
        val center = Coordinate('E', 5)
        val move = Move.GrandAttack(center)

        val result = attackTracker.handle(move, board)

        Assertions.assertTrue(result is MoveResult.GrandResult, "Expected result to be GrandResult")

        val grandResult = result as MoveResult.GrandResult
        Assertions.assertTrue(grandResult.results.isNotEmpty(), "Bomber attack results list should not be empty")
        Assertions.assertEquals(CellStatus.MISS, board.getCellStatus(center))
    }
}