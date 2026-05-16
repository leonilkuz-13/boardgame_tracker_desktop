package UnitTests.ScanTest

import Scan.ScanImpl
import board.BoardImpl
import common.CellStatus
import common.Coordinate
import common.MoveResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScanImplTest {

    private lateinit var scanner: ScanImpl
    private lateinit var board: BoardImpl

    @BeforeEach
    fun setup() {
        scanner = ScanImpl()
        board = BoardImpl()
    }

    @Test
    fun `isValidScan returns true for coordinates within bounds`() {
        assertTrue(scanner.isValidScan(Coordinate('A', 1), board), "Expected A1 to be valid")
        assertTrue(scanner.isValidScan(Coordinate('H', 8), board), "Expected H8 to be valid")
        assertTrue(scanner.isValidScan(Coordinate('O', 15), board), "Expected O15 to be valid")
    }

    @Test
    fun `isValidScan returns false for coordinates out of bounds`() {
        assertFalse(scanner.isValidScan(Coordinate('P', 1), board), "Expected P1 to be invalid (out of bounds)")
        assertFalse(scanner.isValidScan(Coordinate('Z', 99), board), "Expected Z99 to be invalid")
    }

    @Test
    fun `scan out of bounds returns GameError`() {
        val result = scanner.scan(Coordinate('Z', 99), board)

        assertTrue(result is MoveResult.Error.GameError, "Expected GameError for out of bounds scan center")
        val error = result as MoveResult.Error.GameError

        assertEquals("Scanning center is beyond the field boundaries", error.reason)
    }

    @Test
    fun `scan in the center returns a full 5x5 grid and detects ships`() {
        val targetCoord = Coordinate('H', 8)
        board.updateCellStatus(targetCoord, CellStatus.SHIP)

        val result = scanner.scan(Coordinate('G', 7), board)

        assertTrue(result is MoveResult.ScanResult, "Expected ScanResult")
        val scanResult = result as MoveResult.ScanResult

        assertEquals(25, scanResult.info.size, "Expected exactly 25 cells for a center scan")

        assertEquals(CellStatus.SHIP, scanResult.info[targetCoord], "Expected the scanner to detect the SHIP at H8")
    }

    @Test
    fun `scan at the corner returns a truncated grid without crashing`() {
        val result = scanner.scan(Coordinate('A', 1), board)

        assertTrue(result is MoveResult.ScanResult, "Expected ScanResult")
        val scanResult = result as MoveResult.ScanResult

        assertEquals(9, scanResult.info.size, "Expected exactly 9 cells for a corner scan")

        assertTrue(scanResult.info.containsKey(Coordinate('A', 1)))
        assertTrue(scanResult.info.containsKey(Coordinate('C', 3)))

        assertFalse(scanResult.info.containsKey(Coordinate('@', 0)), "Out of bounds coordinates should not be included")
    }
}