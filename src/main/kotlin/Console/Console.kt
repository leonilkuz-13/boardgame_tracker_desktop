package console

import common.Move
import common.MoveResult
import common.PlayerStats

interface Console {
    fun start()
    fun getLeaderBoard(): List<PlayerStats>
    fun getPlayerProfile(name: String): PlayerStats?
    fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>?
}
