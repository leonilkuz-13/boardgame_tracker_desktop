package UnitTests.PlayerTest

import board.BoardImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import player.PlayerImpl

class PlayerImplTest {

    private lateinit var player: PlayerImpl
    private lateinit var myBoard: BoardImpl
    private lateinit var enemyBoard: BoardImpl

    @BeforeEach
    fun setup() {
        myBoard = BoardImpl()
        enemyBoard = BoardImpl()

        player = PlayerImpl("Test Player", myBoard, enemyBoard)
    }

    @Test
    fun `player has correct initial charges`() {
        assertEquals("Test Player", player.name, "Expected name to match")
        assertEquals(2, player.radarCharges, "Expected 2 radar charges initially")
        assertEquals(1, player.bomberCharges, "Expected 1 bomber charge initially")
    }

    @Test
    fun `useRadar decreases radar charges and returns true on success`() {
        val result1 = player.useRadar()
        assertTrue(result1, "Expected useRadar to return true")
        assertEquals(1, player.radarCharges, "Expected 1 radar charge left")

        val result2 = player.useRadar()
        assertTrue(result2, "Expected useRadar to return true")
        assertEquals(0, player.radarCharges, "Expected 0 radar charges left")
    }

    @Test
    fun `useBomber decreases bomber charges and returns true on success`() {
        val result = player.useBomber()
        assertTrue(result, "Expected useBomber to return true")
        assertEquals(0, player.bomberCharges, "Expected 0 bomber charges left")
    }

    @Test
    fun `useRadar returns false when out of charges`() {
        player.useRadar()
        player.useRadar()

        val result = player.useRadar()

        assertFalse(result, "Expected useRadar to return false when no charges left")
        assertEquals(0, player.radarCharges, "Charges should not drop below 0")
    }

    @Test
    fun `useBomber returns false when out of charges`() {
        player.useBomber() // тратим первый (единственный)

        val result = player.useBomber()

        assertFalse(result, "Expected useBomber to return false when no charges left")
        assertEquals(0, player.bomberCharges, "Charges should not drop below 0")
    }
}