package UnitTests.BoardTest

import battleship.BattleWagon
import board.BoardImpl
import common.CellStatus
import common.Coordinate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoardImplTest {

    private lateinit var board: BoardImpl

    @BeforeEach
    fun setup() {
        board = BoardImpl()
    }

    @Test
    fun `isWithinBounds returns true for valid coordinates`() {
        assertTrue(board.isWithinBounds(Coordinate('A', 1)), "Expected A1 to be within bounds")
        assertTrue(board.isWithinBounds(Coordinate('O', 15)), "Expected O15 to be within bounds")
        assertTrue(board.isWithinBounds(Coordinate('H', 8)), "Expected H8 to be within bounds")
    }

    @Test
    fun `isWithinBounds returns false for invalid coordinates`() {
        assertFalse(board.isWithinBounds(Coordinate('P', 1)), "Expected P1 to be out of bounds")
        assertFalse(board.isWithinBounds(Coordinate('A', 16)), "Expected A16 to be out of bounds")
        assertFalse(board.isWithinBounds(Coordinate('Z', 99)), "Expected Z99 to be out of bounds")
    }

    @Test
    fun `updateCellStatus changes cell status correctly`() {
        val coord = Coordinate('C', 5)
        board.updateCellStatus(coord, CellStatus.HIT)

        assertEquals(CellStatus.HIT, board.getCellStatus(coord), "Expected cell C5 to be updated to HIT")
    }

    @Test
    fun `updateCellStatus throws exception for out of bounds coordinate`() {
        val invalidCoord = Coordinate('Z', 1)

        assertThrows<IllegalArgumentException>("Expected IllegalArgumentException for out of bounds coordinate") {
            board.updateCellStatus(invalidCoord, CellStatus.MISS)
        }
    }

    @Test
    fun `getCellStatus throws exception for out of bounds coordinate`() {
        val invalidCoord = Coordinate('Z', 1)

        assertThrows<IllegalStateException>("Expected IllegalStateException for out of bounds coordinate") {
            board.getCellStatus(invalidCoord)
        }
    }

    @Test
    fun `addShip and getShips work correctly`() {
        val coords = listOf(Coordinate('A', 1), Coordinate('A', 2))
        val ship = BattleWagon(coords)

        board.addShip(ship)
        val ships = board.getShips()

        assertEquals(1, ships.size, "Expected exactly 1 ship on the board")
        assertEquals(ship, ships.first(), "Expected the retrieved ship to match the added one")
    }

    @Test
    fun `getShipAt returns correct ship or null if empty`() {
        val coord1 = Coordinate('B', 2)
        val coord2 = Coordinate('B', 3)
        val ship = BattleWagon(listOf(coord1, coord2))

        board.addShip(ship)

        assertEquals(ship, board.getShipAt(coord1), "Expected to find ship at B2")
        assertNull(board.getShipAt(Coordinate('C', 2)), "Expected null for an empty coordinate C2")
    }

    @Test
    fun `hasAliveShips returns correct state`() {
        val coord = Coordinate('D', 4)
        val ship = BattleWagon(listOf(coord))

        board.addShip(ship)
        assertTrue(board.hasAliveShips(), "Expected board to have alive ships initially")

        ship.receiveHit(coord)

        assertFalse(board.hasAliveShips(), "Expected board to have NO alive ships after the only ship is sunk")
    }
}