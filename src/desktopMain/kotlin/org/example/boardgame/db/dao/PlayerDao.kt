package org.example.boardgame.db.dao

import org.example.boardgame.db.entities.PlayerEntity

interface PlayerDao {
    fun getPlayerByName(name: String): PlayerEntity?
    fun getTopPlayers(limit: Int): List<PlayerEntity>
    fun createPlayer(name: String)
    fun updateStats(name: String, isWin: Boolean)
}
