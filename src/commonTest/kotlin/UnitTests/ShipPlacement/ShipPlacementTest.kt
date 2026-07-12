package UnitTests.ShipPlacement

import ShipPlacement.ShipPlacementImpl
import battleship.BattleWagon
import battleship.Submarine
import board.BoardImpl
import common.CellStatus
import common.Coordinate
import common.MoveResult
import kotlin.test.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class ShipPlacementImplTest {

    private lateinit var placement: ShipPlacementImpl
    private lateinit var board: BoardImpl

    @BeforeTest
    fun setup() {
        placement = ShipPlacementImpl()
        board = BoardImpl()
    }

    @Test
    fun `placeShip successfully installs a ship and creates borders`() {
        val shipCoords = listOf(Coordinate('D', 4))
        val submarine = Submarine(shipCoords)

        val result = placement.placeShip(submarine, board)

        assertTrue(result is MoveResult.ShipInstall, "Expected ShipInstall success result")
        val installResult = result as MoveResult.ShipInstall

        assertEquals(CellStatus.SHIP, board.getCellStatus(Coordinate('D', 4)), "Expected D4 to be SHIP")

        assertEquals(8, installResult.borderCoordinates.size, "Expected exactly 8 borders around a single cell ship")

        assertEquals(CellStatus.BORDER, board.getCellStatus(Coordinate('C', 3)), "Expected neighbor C3 to be BORDER")

        assertEquals(1, board.getShips().size, "Expected exactly 1 ship on the board")
    }

    @Test
    fun `placeShip fails when exceeding maximum allowed ships of a type`() {
        val bw1 = BattleWagon(listOf(Coordinate('A', 1), Coordinate('A', 2)))
        val result1 = placement.placeShip(bw1, board)
        assertTrue(result1 is MoveResult.ShipInstall, "First BattleWagon should be installed successfully")

        val bw2 = BattleWagon(listOf(Coordinate('H', 8), Coordinate('H', 9)))
        val result2 = placement.placeShip(bw2, board)

        assertTrue(result2 is MoveResult.Error.GameError, "Expected GameError when exceeding ship limits")
        val error = result2 as MoveResult.Error.GameError
        assertEquals("Invalid coordinates for ship placement", error.reason)
    }

    @Test
    fun `placeShip fails when placing out of bounds`() {
        val submarine = Submarine(listOf(Coordinate('Z', 99)))

        val result = placement.placeShip(submarine, board)

        assertTrue(result is MoveResult.Error.GameError, "Expected GameError for out of bounds placement")
    }

    @Test
    fun `placeShip fails when placing on another ship or its border`() {
        val sub1 = Submarine(listOf(Coordinate('B', 2)))
        placement.placeShip(sub1, board)

        val sub2 = Submarine(listOf(Coordinate('B', 2)))
        val resultCollision = placement.placeShip(sub2, board)

        val sub3 = Submarine(listOf(Coordinate('C', 2)))
        val resultBorder = placement.placeShip(sub3, board)

        assertTrue(resultCollision is MoveResult.Error.GameError, "Cannot place on top of another ship")
        assertTrue(resultBorder is MoveResult.Error.GameError, "Cannot place on the border (halo) of another ship")
    }
}