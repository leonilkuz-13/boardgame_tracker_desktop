package org.example.boardgame.repository

import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {
    var dbUrl: String = "jdbc:sqlite:battleship.db"

    fun getConnection(): Connection {
        return DriverManager.getConnection(dbUrl)
    }

    fun initDatabase() {
        val createPlayers = """
            CREATE TABLE IF NOT EXISTS "Players" (
                "name"    TEXT NOT NULL UNIQUE,
                "wins"    INTEGER NOT NULL DEFAULT 0,
                "losses"  INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY("name")
            );
        """.trimIndent()

        val createMatches = """
            CREATE TABLE IF NOT EXISTS "Matches" (
                "id"    INTEGER NOT NULL,
                "player1_name"    TEXT NOT NULL,
                "player2_name"    TEXT NOT NULL,
                "winner_name"    TEXT NOT NULL,
                PRIMARY KEY("id" AUTOINCREMENT)
            );
        """.trimIndent()

        val createMoves = """
            CREATE TABLE IF NOT EXISTS "Moves" (
                "id"    INTEGER NOT NULL,
                "match_id"    INTEGER NOT NULL,
                "turn_number"    INTEGER NOT NULL,
                "type_action"    TEXT NOT NULL,
                "coordinates"    TEXT NOT NULL,
                "result_status"    TEXT NOT NULL,
                "type_ship"    TEXT,
                PRIMARY KEY("id" AUTOINCREMENT)
            );
        """.trimIndent()

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(createPlayers)
                stmt.execute(createMatches)
                stmt.execute(createMoves)
            }
        }
    }
}