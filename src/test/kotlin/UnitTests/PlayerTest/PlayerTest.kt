package UnitTests.PlayerTest

import board.BoardImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    fun `isUseRadar decreases radar charges`() {
        player.isUseRadar()
        assertEquals(1, player.radarCharges, "Expected 1 radar charge left")

        player.isUseRadar()
        assertEquals(0, player.radarCharges, "Expected 0 radar charges left")
    }

    @Test
    fun `isUseBomber decreases bomber charges`() {
        player.isUseBomber()
        assertEquals(0, player.bomberCharges, "Expected 0 bomber charges left")
    }

    @Test
    fun `isUseRadar throws ItemDepletedException when out of charges`() {
        player.isUseRadar()
        player.isUseRadar()

        val exception = assertThrows<PlayerImpl.ItemDepletedException>("Expected ItemDepletedException") {
            player.isUseRadar()
        }

        assertEquals("you don't have radars", exception.message)
    }

    @Test
    fun `isUseBomber throws ItemDepletedException when out of charges`() {
        player.isUseBomber()

        val exception = assertThrows<PlayerImpl.ItemDepletedException>("Expected ItemDepletedException") {
            player.isUseBomber()
        }

        assertEquals("you don't have bomber", exception.message)
    }
}