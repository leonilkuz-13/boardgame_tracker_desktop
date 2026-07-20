package org.example.boardgame.db.dao

import org.example.boardgame.repository.DatabaseManager
import org.example.boardgame.db.entities.MatchEntity
import org.example.boardgame.db.entities.MoveEntity
import java.sql.Connection
import java.sql.Statement

class JdbcHistoryDao : HistoryDao {
    
    private fun getNewConnection(): Connection = DatabaseManager.getConnection()

    override fun insertMatch(connection: Connection, p1: String, p2: String, winner: String): Int {
        val query = "INSERT INTO MATCHES (player1_name, player2_name, winner_name) VALUES (?, ?, ?)"
        return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, p1)
            stmt.setString(2, p2)
            stmt.setString(3, winner)
            stmt.executeUpdate()
            val keys = stmt.generatedKeys
            if (keys.next()) keys.getInt(1) else -1
        }
    }

    override fun insertMove(connection: Connection, matchId: Int, turn: Int, action: String, coords: String, result: String, shipType: String?) {
        val query = "INSERT INTO Moves (match_id, turn_number, type_action, coordinates, result_status, type_ship) VALUES (?, ?, ?, ?, ?, ?)"
        connection.prepareStatement(query).use { stmt ->
            stmt.setInt(1, matchId)
            stmt.setInt(2, turn)
            stmt.setString(3, action)
            stmt.setString(4, coords)
            stmt.setString(5, result)
            stmt.setString(6, shipType)
            stmt.executeUpdate()
        }
    }

    override fun getMoves(matchId: Int): List<MoveEntity> {
        val query = "SELECT id, match_id, turn_number, type_action, coordinates, result_status, type_ship FROM MOVES WHERE match_id = ? ORDER BY turn_number ASC"
        val moves = mutableListOf<MoveEntity>()
        return getNewConnection().use { connection ->
            connection.prepareStatement(query).use { stmt ->
                stmt.setInt(1, matchId)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    moves.add(
                        MoveEntity(
                            id = rs.getInt("id"),
                            matchId = rs.getInt("match_id"),
                            turnNumber = rs.getInt("turn_number"),
                            typeAction = rs.getString("type_action"),
                            coordinates = rs.getString("coordinates"),
                            resultStatus = rs.getString("result_status"),
                            typeShip = rs.getString("type_ship")
                        )
                    )
                }
                moves
            }
        }
    }

    override fun getMatch(matchId: Int): MatchEntity? {
        val query = "SELECT id, player1_name, player2_name, winner_name FROM MATCHES WHERE id = ?"
        return getNewConnection().use { connection ->
            connection.prepareStatement(query).use { stmt ->
                stmt.setInt(1, matchId)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    MatchEntity(
                        id = rs.getInt("id"),
                        player1Name = rs.getString("player1_name"),
                        player2Name = rs.getString("player2_name"),
                        winnerName = rs.getString("winner_name")
                    )
                } else null
            }
        }
    }
}
