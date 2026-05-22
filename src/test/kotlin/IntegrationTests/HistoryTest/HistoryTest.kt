package HistoryTest

import Repository.DatabaseManager
import Repository.HistoryImpl
import battleship.*
import common.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class HistoryTest {

    private lateinit var history: HistoryImpl

    @BeforeEach
    fun clearData() {
        // 1. Переключаем Менеджер на тестовую базу
        DatabaseManager.dbUrl = "jdbc:sqlite:test_battleship.db"
        DatabaseManager.initDatabase()
        history = HistoryImpl()

        DatabaseManager.getConnection().use { conn ->
            val statement = conn.createStatement()
            statement.execute("DELETE FROM MOVES")
            statement.execute("DELETE FROM MATCHES")
            statement.execute("DELETE FROM Players")
        }
    }

    @Test
    fun `full match save and replay test`() {
        val player1 = "Commander_77"
        val player2 = "AI_DeepBlue"

        DatabaseManager.getConnection().use { conn ->
            val stmt = conn.prepareStatement("INSERT INTO Players (name) VALUES (?), (?)")
            stmt.setString(1, player1)
            stmt.setString(2, player2)
            stmt.executeUpdate()
        }

        val coordA1 = Coordinate('A', 1)
        val coordB2 = Coordinate('B', 2)
        val coordJ10 = Coordinate('J', 10)

        val testLog = listOf(
            Pair(
                Move.Install(Submarine(listOf(coordA1)), listOf(coordA1)),
                MoveResult.ShipInstall(listOf(coordA1), emptySet(), ShipType.SUBMARINE)
            ),
            Pair(
                Move.SingleAttack(coordB2),
                MoveResult.Success.Hit(coordB2)
            ),
            Pair(
                Move.SingleAttack(coordA1),
                MoveResult.Success.Sunk(coordA1, emptySet(), ShipType.SUBMARINE)
            ),
            Pair(
                Move.SingleAttack(coordJ10),
                MoveResult.Success.Miss(coordJ10)
            )
        )

        history.saveMatch(player1, player2, player1, testLog)

        val matchId = DatabaseManager.getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery("SELECT id FROM MATCHES LIMIT 1")
            if (rs.next()) rs.getInt("id") else fail("Match not found in database")
        }

        val restoredLog = history.getMatchReplay(matchId)

        assertNotNull(restoredLog, "Replay should not be null")
        assertEquals(testLog.size, restoredLog!!.size, "Number of moves does not match")

        val (move1, res1) = restoredLog[0]
        assertTrue(move1 is Move.Install)
        assertEquals('A', (move1 as Move.Install).coordinates[0].x)
        assertEquals(ShipType.SUBMARINE, (res1 as MoveResult.ShipInstall).shipType)

        val (move2, res2) = restoredLog[1]
        assertTrue(res2 is MoveResult.Success.Hit)
        assertEquals('B', (res2 as MoveResult.Success.Hit).coordinate.x)
        assertEquals(2, res2.coordinate.y)

        val (move3, res3) = restoredLog[2]
        assertEquals(ShipType.SUBMARINE, (res3 as MoveResult.Success.Sunk).shipType)
    }

    @Test
    fun `non-existent match should return empty list`() {
        val result = history.getMatchReplay(9999)
        assertTrue(result?.isEmpty() ?: true)
    }
}