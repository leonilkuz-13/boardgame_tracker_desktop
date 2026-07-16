package org.example.boardgame.db.daos

import androidx.room.*
import org.example.boardgame.db.entities.PlayerEntity

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Players WHERE name = :playerName")
    fun getPlayerByName(playerName: String): PlayerEntity?

    @Query("SELECT * FROM Players ORDER BY wins DESC LIMIT :limit")
    fun getTopPlayers(limit: Int): List<PlayerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlayer(player: PlayerEntity)

    @Query("UPDATE Players SET wins = wins + 1 WHERE name = :playerName")
    fun incrementWins(playerName: String)

    @Query("UPDATE Players SET losses = losses + 1 WHERE name = :playerName")
    fun incrementLosses(playerName: String)
}
