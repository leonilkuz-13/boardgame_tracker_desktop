package unittests.baseshiptest

import battleship.BattleWagon
import common.Coordinate
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BaseShipTest {
    @Test
    fun `new ship is not sunk`() {

        val coords = listOf(Coordinate('A', 1), Coordinate('A', 2), Coordinate('A', 3))
        val ship = BattleWagon(coords)
        assertFalse(ship.isSunk(), "Expected new ship to not be sunk")
    }

    @Test
    fun `ship is not sunk after partial hit`() {
        val coords = listOf(Coordinate('B', 1), Coordinate('B', 2))
        val ship = BattleWagon(coords)

        ship.receiveHit(Coordinate('B', 1))

        assertFalse(ship.isSunk(), "Expected ship to be alive after partial hit")
    }

    @Test
    fun `ship sinks when all coordinates are hit`() {
        val coords = listOf(Coordinate('C', 1), Coordinate('C', 2))
        val ship = BattleWagon(coords)

        ship.receiveHit(Coordinate('C', 1))
        ship.receiveHit(Coordinate('C', 2))

        assertTrue(ship.isSunk(), "Expected ship to be sunk after all coordinates are hit")
    }

    @Test
    fun `ship ignores hits on missing coordinates`() {
        val coords = listOf(Coordinate('D', 1))
        val ship = BattleWagon(coords)

        ship.receiveHit(Coordinate('D', 2))

        assertFalse(ship.isSunk(), "Expected ship to ignore missed hits and stay alive")
    }

    @Test
    fun `hitting the same coordinate twice does not sink a multi-deck ship`() {
        val coords = listOf(Coordinate('E', 1), Coordinate('E', 2))
        val ship = BattleWagon(coords)

        ship.receiveHit(Coordinate('E', 1))
        ship.receiveHit(Coordinate('E', 1))

        assertFalse(ship.isSunk(), "Expected ship to survive duplicate hits on the same coordinate")
    }
}