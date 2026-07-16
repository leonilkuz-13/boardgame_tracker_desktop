package org.example.boardgame.db.impl

import common.PlayerStats
import org.example.boardgame.db.daos.PlayerDao
import org.example.boardgame.db.entities.PlayerEntity
import repository.Statistics

class AndroidStatisticsImpl(private val playerDao: PlayerDao) : Statistics {

    override fun getPlayerStats(playerName: String): PlayerStats? {
        val entity = playerDao.getPlayerByName(playerName) ?: return null
        val gamesPlayed = entity.wins + entity.losses
        val winRate = if (gamesPlayed > 0) (entity.wins * 100) / gamesPlayed else 0
        return PlayerStats(
            playerName = entity.name,
            gamesPlayed = gamesPlayed,
            gamesWon = entity.wins,
            winRate = winRate,
            gamesLost = entity.losses
        )
    }

    override fun getTopPlayers(limit: Int): List<PlayerStats> {
        return playerDao.getTopPlayers(limit).map { entity ->
            val gamesPlayed = entity.wins + entity.losses
            val winRate = if (gamesPlayed > 0) (entity.wins * 100) / gamesPlayed else 0
            PlayerStats(
                playerName = entity.name,
                gamesPlayed = gamesPlayed,
                gamesWon = entity.wins,
                winRate = winRate,
                gamesLost = entity.losses
            )
        }
    }

    override fun saveMatchResult(playerName: String, isWin: Boolean) {
        if (isWin) {
            playerDao.incrementWins(playerName)
        } else {
            playerDao.incrementLosses(playerName)
        }
    }

    override fun createPlayer(playerName: String) {
        playerDao.insertPlayer(PlayerEntity(playerName))
    }
}
