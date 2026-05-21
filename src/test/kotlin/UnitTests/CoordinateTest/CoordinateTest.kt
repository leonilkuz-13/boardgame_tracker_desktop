package UnitTests.CoordinateTest

import common.Coordinate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CoordinateTest {
    @Test
    fun `parse valid strings returns correct Coordinate`() {
        val coord1 = Coordinate.parse("A1")
        assertNotNull(coord1, "Expected valid coordinate for 'A1'")
        assertEquals('A', coord1?.x)
        assertEquals(1, coord1?.y)

        val coord2 = Coordinate.parse(" o15 ") // With spaces and lowercase
        assertNotNull(coord2, "Expected valid coordinate for ' o15 '")
        assertEquals('O', coord2?.x)
        assertEquals(15, coord2?.y)
    }

    @Test
    fun `parse invalid strings or out of bounds returns null`() {
        assertNull(Coordinate.parse(""), "Expected null for empty string")
        assertNull(Coordinate.parse("A"), "Expected null for string without numbers")
        assertNull(Coordinate.parse("123"), "Expected null for string without letters")
        assertNull(Coordinate.parse("Z99"), "Expected null for out of bounds coordinate 'Z99'")
        assertNull(Coordinate.parse("P1"), "Expected null for out of bounds coordinate 'P1'")
        assertNull(Coordinate.parse("A16"), "Expected null for out of bounds coordinate 'A16'")
    }

    @Test
    fun `isValid returns true for coordinates within bounds`() {
        assertTrue(Coordinate('A', 1).isValid(), "Expected A1 to be valid")
        assertTrue(Coordinate('O', 15).isValid(), "Expected O15 to be valid")
        assertTrue(Coordinate('H', 8).isValid(), "Expected H8 to be valid")
    }

    @Test
    fun `isValid returns false for coordinates out of bounds`() {
        assertFalse(Coordinate('P', 1).isValid(), "Expected P1 to be invalid")
        assertFalse(Coordinate('A', 0).isValid(), "Expected A0 to be invalid")
        assertFalse(Coordinate('A', 16).isValid(), "Expected A16 to be invalid")
        assertFalse(Coordinate('@', 5).isValid(), "Expected @5 to be invalid")
    }

    @Test
    fun `dotAdjacentTo returns true for neighbor coordinates`() {
        val center = Coordinate('B', 2)

        assertTrue(center.dotAdjacentTo(Coordinate('B', 1)), "Expected B1 to be adjacent to B2")
        assertTrue(center.dotAdjacentTo(Coordinate('C', 2)), "Expected C2 to be adjacent to B2")

        assertTrue(center.dotAdjacentTo(Coordinate('C', 3)), "Expected C3 to be adjacent to B2")
        assertTrue(center.dotAdjacentTo(Coordinate('A', 1)), "Expected A1 to be adjacent to B2")
    }

    @Test
    fun `dotAdjacentTo returns false for non-neighbor coordinates`() {
        val center = Coordinate('B', 2)

        assertFalse(center.dotAdjacentTo(Coordinate('B', 2)), "Expected B2 to NOT be adjacent to itself")
        assertFalse(center.dotAdjacentTo(Coordinate('B', 4)), "Expected B4 to NOT be adjacent to B2")
        assertFalse(center.dotAdjacentTo(Coordinate('E', 5)), "Expected E5 to NOT be adjacent to B2")
    }

    @Test
    fun `getNeighbors returns exactly 8 valid coordinates for a center cell`() {
        val center = Coordinate('B', 2)
        val neighbors = center.getNeighbors()

        assertEquals(8, neighbors.size, "Expected exactly 8 neighbors for a center cell")
        assertFalse(neighbors.contains(center), "Neighbors list should not contain the original cell itself")

        for (neighbor in neighbors) {
            assertTrue(center.dotAdjacentTo(neighbor), "Expected neighbor $neighbor to be adjacent")
        }
    }

    @Test
    fun `getNeighbors returns only valid coordinates for corner cells`() {
        val corner = Coordinate('A', 1)
        val neighbors = corner.getNeighbors()

        assertEquals(3, neighbors.size, "Expected exactly 3 neighbors for a corner cell")
        assertTrue(neighbors.contains(Coordinate('A', 2)))
        assertTrue(neighbors.contains(Coordinate('B', 1)))
        assertTrue(neighbors.contains(Coordinate('B', 2)))
    }
}