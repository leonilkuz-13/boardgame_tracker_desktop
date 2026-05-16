package StatisticsTest

import Repository.StatisticsImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.sql.DriverManager

class StatisticsImplTest {

    private lateinit var statistics: StatisticsImpl
    private val dbUrl = "jdbc:sqlite:battleship.db"

    @BeforeEach
    fun setup() {
        statistics = StatisticsImpl()
        DriverManager.getConnection(dbUrl).use { conn ->
            conn.createStatement().execute("DELETE FROM Players")
        }
    }

    @Test
    fun `createPlayer should initialize new player with zero stats`() {
        val name = "Newbie"
        statistics.createPlayer(name)

        val stats = statistics.getPlayerStats(name)

        assertNotNull(stats)
        assertEquals(name, stats?.playerName)
        assertEquals(0, stats?.gamesPlayed)
        assertEquals(0, stats?.gamesWon)
        assertEquals(0, stats?.winRate)
    }

    @Test
    fun `saveMatchResult should correctly increment wins and losses`() {
        val name = "Fighter"
        statistics.createPlayer(name)

        statistics.saveMatchResult(name, true)
        statistics.saveMatchResult(name, true)
        statistics.saveMatchResult(name, false)

        val stats = statistics.getPlayerStats(name)

        assertEquals(3, stats?.gamesPlayed)
        assertEquals(2, stats?.gamesWon)
        assertEquals(1, stats?.gamesLost)
        assertEquals(66, stats?.winRate)
    }

    @Test
    fun `getTopPlayers should sort by winRate and then by wins`() {
        statistics.createPlayer("Pro")
        statistics.saveMatchResult("Pro", true)
        statistics.saveMatchResult("Pro", true)

        statistics.createPlayer("Lucky")
        statistics.saveMatchResult("Lucky", true)

        statistics.createPlayer("Noob")
        statistics.saveMatchResult("Noob", false)

        val top = statistics.getTopPlayers(3)

        assertEquals(3, top.size)

        assertEquals("Pro", top[0].playerName)
        assertEquals("Lucky", top[1].playerName)
        assertEquals("Noob", top[2].playerName)
    }

    @Test
    fun `getTopPlayers should respect the limit`() {
        listOf("A", "B", "C", "D").forEach { statistics.createPlayer(it) }

        val top = statistics.getTopPlayers(2)
        assertEquals(2, top.size)
    }
}
