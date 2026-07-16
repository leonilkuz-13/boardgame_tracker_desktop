package org.example.boardgame.db.dao

import org.example.boardgame.repository.DatabaseManager
import org.example.boardgame.db.entities.PlayerEntity
import java.sql.Connection

class JdbcPlayerDao : PlayerDao {
    
    private fun getConnection(): Connection = DatabaseManager.getConnection()

    override fun getPlayerByName(name: String): PlayerEntity? {
        val query = "SELECT wins, losses FROM Players WHERE name = ?"
        return getConnection().use { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, name)
                val result = statement.executeQuery()
                if (result.next()) {
                    PlayerEntity(
                        name = name,
                        wins = result.getInt("wins"),
                        losses = result.getInt("losses")
                    )
                } else null
            }
        }
    }

    override fun getTopPlayers(limit: Int): List<PlayerEntity> {
        val query = "SELECT name, wins, losses FROM Players ORDER BY wins DESC LIMIT ?"
        val players = mutableListOf<PlayerEntity>()
        return getConnection().use { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.setInt(1, limit)
                val result = statement.executeQuery()
                while (result.next()) {
                    players.add(
                        PlayerEntity(
                            name = result.getString("name"),
                            wins = result.getInt("wins"),
                            losses = result.getInt("losses")
                        )
                    )
                }
                players
            }
        }
    }

    override fun createPlayer(name: String) {
        val query = "INSERT OR IGNORE INTO Players (name, wins, losses) VALUES (?, 0, 0)"
        getConnection().use { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, name)
                statement.executeUpdate()
            }
        }
    }

    override fun updateStats(name: String, isWin: Boolean) {
        val column = if (isWin) "wins" else "losses"
        val query = "UPDATE Players SET $column = $column + 1 WHERE name = ?"
        getConnection().use { connection ->
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, name)
                statement.executeUpdate()
            }
        }
    }
}
