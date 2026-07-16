package org.example.boardgame.repository

import common.PlayerStats
import repository.Statistics
import org.example.boardgame.db.dao.PlayerDao
import org.example.boardgame.db.entities.PlayerEntity

class StatisticsImpl(private val playerDao: PlayerDao) : Statistics {

    override fun getPlayerStats(playerName: String): PlayerStats? {
        return playerDao.getPlayerByName(playerName)?.toDomain()
    }

    override fun getTopPlayers(limit: Int): List<PlayerStats> {
        return playerDao.getTopPlayers(limit).map { it.toDomain() }
    }

    override fun saveMatchResult(playerName: String, isWin: Boolean) {
        playerDao.updateStats(playerName, isWin)
    }

    override fun createPlayer(playerName: String) {
        playerDao.createPlayer(playerName)
    }

    private fun PlayerEntity.toDomain(): PlayerStats {
        val gamesPlayed = wins + losses
        val winRate = if (gamesPlayed > 0) (wins * 100) / gamesPlayed else 0
        return PlayerStats(
            playerName = name,
            gamesPlayed = gamesPlayed,
            gamesWon = wins,
            winRate = winRate,
            gamesLost = losses
        )
    }
}
