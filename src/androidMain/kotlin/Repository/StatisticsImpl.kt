package Repository

import android.content.ContentValues
import common.PlayerStats

class StatisticsImpl(context: android.content.Context) : Statistics {

    private val dbHelper = DatabaseHelper(context)

    override fun getPlayerStats(playerName: String): PlayerStats? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT wins, losses FROM Players WHERE name = ?",
            arrayOf(playerName)
        )
        return cursor.use {
            if (it.moveToFirst()) {
                val gamesWon = it.getInt(0)
                val gamesLost = it.getInt(1)
                val gamesPlayed = gamesWon + gamesLost
                val winRate = if (gamesPlayed > 0) (gamesWon * 100) / gamesPlayed else 0
                PlayerStats(playerName, gamesPlayed, gamesWon, winRate, gamesLost)
            } else null
        }
    }

    override fun getTopPlayers(limit: Int): List<PlayerStats> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT name, wins, losses FROM Players", null)
        val allStats = mutableListOf<PlayerStats>()
        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(0)
                val gamesWon = it.getInt(1)
                val gamesLost = it.getInt(2)
                val gamesPlayed = gamesWon + gamesLost
                val winRate = if (gamesPlayed > 0) (gamesWon * 100) / gamesPlayed else 0
                allStats.add(PlayerStats(name, gamesPlayed, gamesWon, winRate, gamesLost))
            }
        }
        return allStats.sortedWith(
            compareByDescending<PlayerStats> { it.winRate }.thenByDescending { it.gamesWon }
        ).take(limit)
    }

    override fun saveMatchResult(playerName: String, isWin: Boolean) {
        val db = dbHelper.writableDatabase
        val column = if (isWin) "wins" else "losses"
        val cv = ContentValues().apply {
            put(column, 1)
        }
        db.execSQL("UPDATE Players SET $column = $column + 1 WHERE name = ?", arrayOf(playerName))
    }

    override fun createPlayer(playerName: String) {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT OR IGNORE INTO Players (name) VALUES (?)", arrayOf(playerName))
    }
}
