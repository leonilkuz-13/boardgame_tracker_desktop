package gamemanager.interfaces

import common.PlayerStats
import common.ManagerResult

interface PlayerManager {
    fun loginPlayer(playerName: String): ManagerResult
    fun getPlayerStats(name: String): PlayerStats?
    fun getLeaderboard(): List<PlayerStats>
    fun getPlayer1Name(): String?
    fun getPlayer2Name(): String?
    fun getCurrentPlayerName(): String
}
