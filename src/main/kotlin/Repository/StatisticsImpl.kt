package Repository

import common.PlayerStats
import java.sql.DriverManager

class StatisticsImpl : Statistics {

    override fun getPlayerStats(playerName: String): PlayerStats? {
        val url = "jdbc:sqlite:battleship.db"
        val query = "SELECT wins, losses FROM Players WHERE name = ?"

        try {
            DriverManager.getConnection(url).use { connection ->
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, playerName)
                    val result = statement.executeQuery()

                    if (result.next()) {
                        val gamesWon = result.getInt("wins")
                        val gamesLost = result.getInt("losses")
                        val gamesPlayed = gamesWon + gamesLost

                        val winRate = if (gamesPlayed > 0) (gamesWon * 100) / gamesPlayed else 0 // так и надо

                        return PlayerStats(
                            playerName = playerName,
                            gamesPlayed = gamesPlayed,
                            gamesWon = gamesWon,
                            winRate = winRate,
                            gamesLost = gamesLost
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("> Error: ${e.message}")
        }
        return null
    }

    override fun getTopPlayers(limit: Int): List<PlayerStats> {
        val url = "jdbc:sqlite:battleship.db"
        val query = "SELECT name, wins, losses FROM Players"
        val allStats = mutableListOf<PlayerStats>()

        try {
            DriverManager.getConnection(url).use { connection ->
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()

                    while (result.next()) {
                        val name = result.getString("name")
                        val gamesWon = result.getInt("wins")
                        val gamesLost = result.getInt("losses")
                        val gamesPlayed = gamesWon + gamesLost

                        val winRate = if (gamesPlayed > 0) (gamesWon * 100) / gamesPlayed else 0

                        allStats.add(PlayerStats(name, gamesPlayed, gamesWon, winRate, gamesLost))
                    }
                }
            }
        } catch (e: Exception) {
            println("> Error: ${e.message}")
            return emptyList()
        }

        return allStats.sortedWith(compareByDescending<PlayerStats> { it.winRate }.thenByDescending { it.gamesWon }).take(limit)
    }

    override fun saveMatchResult(playerName: String, isWin: Boolean) {
        val url = "jdbc:sqlite:battleship.db"
        val updateQuery = if (isWin) {
            "UPDATE Players SET wins = wins + 1 WHERE name = ?"
        } else {
            "UPDATE Players SET losses = losses + 1 WHERE name = ?"
        }

        try {
            DriverManager.getConnection(url).use { connection ->
                connection.prepareStatement(updateQuery).use { statement ->
                    statement.setString(1, playerName)
                    val rowsAffected = statement.executeUpdate()

                    if (rowsAffected > 0) {
                        val resultType = if (isWin) "win" else "loss" // немного поправил опечатку loos -> loss
                        println("> '$playerName' credited $resultType.")
                    } else {
                        println("> The player '$playerName' was not found in the database")
                    }
                }
            }
        } catch (e: Exception) {
            println("> Error: ${e.message}")
        }
    }

    override fun createPlayer(playerName: String) {
        val url = "jdbc:sqlite:battleship.db"
        val insertQuery = "INSERT OR IGNORE INTO Players (name) VALUES (?)"

        try {
            DriverManager.getConnection(url).use { connection ->
                connection.prepareStatement(insertQuery).use { statement ->
                    statement.setString(1, playerName)
                    val rowsAffected = statement.executeUpdate()
                    if (rowsAffected > 0) {
                        println("> Player registered: '$playerName'")
                    } else {
                        println("> Welcome back, '$playerName'!")
                    }
                }
            }
        } catch (e: Exception) {
            println("> Error: ${e.message}")
        }
    }
}