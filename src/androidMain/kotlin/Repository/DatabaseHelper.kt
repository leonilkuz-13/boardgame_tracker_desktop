package Repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_PLAYERS)
        db.execSQL(CREATE_MATCHES)
        db.execSQL(CREATE_MOVES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Moves")
        db.execSQL("DROP TABLE IF EXISTS Matches")
        db.execSQL("DROP TABLE IF EXISTS Players")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "battleship.db"
        const val DATABASE_VERSION = 1

        private val CREATE_PLAYERS = """
            CREATE TABLE IF NOT EXISTS "Players" (
                "name"    TEXT NOT NULL UNIQUE,
                "wins"    INTEGER NOT NULL DEFAULT 0,
                "losses"  INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY("name")
            )
        """.trimIndent()

        private val CREATE_MATCHES = """
            CREATE TABLE IF NOT EXISTS "Matches" (
                "id"    INTEGER NOT NULL,
                "player1_name"    TEXT NOT NULL,
                "player2_name"    TEXT NOT NULL,
                "winner_name"    TEXT NOT NULL,
                PRIMARY KEY("id" AUTOINCREMENT)
            )
        """.trimIndent()

        private val CREATE_MOVES = """
            CREATE TABLE IF NOT EXISTS "Moves" (
                "id"    INTEGER NOT NULL,
                "match_id"    INTEGER NOT NULL,
                "turn_number"    INTEGER NOT NULL,
                "type_action"    TEXT NOT NULL,
                "coordinates"    TEXT NOT NULL,
                "result_status"    TEXT NOT NULL,
                "type_ship"    TEXT,
                PRIMARY KEY("id" AUTOINCREMENT)
            )
        """.trimIndent()
    }
}
