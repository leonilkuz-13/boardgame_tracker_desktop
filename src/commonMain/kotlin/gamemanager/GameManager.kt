package gamemanager

import gamemanager.interfaces.PlayerManager
import gamemanager.interfaces.MatchManager
import gamemanager.interfaces.ViewManager
import common.CellStatus
import repository.MatchSummary

interface GameManager : PlayerManager, MatchManager, ViewManager {
    // Compatibility methods (can be removed if unused)
    fun getMyBoardStatusGrid(): List<List<CellStatus>>
    fun getEnemyBoardStatusGrid(): List<List<CellStatus>>
    fun getMatchSummary(matchId: Int): MatchSummary?
}
