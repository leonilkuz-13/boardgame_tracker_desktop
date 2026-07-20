package org.example.boardgame.console

import common.resolveSpecialShape
import gamemanager.GameManagerImpl
import repository.History
import repository.Statistics
import org.example.boardgame.repository.HistoryImpl
import org.example.boardgame.repository.StatisticsImpl
import org.example.boardgame.db.dao.JdbcPlayerDao
import org.example.boardgame.db.dao.JdbcHistoryDao
import battleship.*
import common.*

class ConsoleImpl : Console {
	private val stat: Statistics = StatisticsImpl(JdbcPlayerDao())
	private val history: History = HistoryImpl(JdbcHistoryDao())
	private val gameManager = GameManagerImpl(stat, history)

	override fun start() {
		println("> Welcome to the Board Game Tracker!")
		while (true) {
			println("> Enter the command:")
			println("> 1 -- Start a new game")
			println("> 2 -- View player statistics")
			println("> 3 -- Get leaderboard statistics")
			println("> 0 -- Exit")

			when (safeReadCommand()) {
				"1" -> {
					if (startNewGame() is ManagerResult.Success) {
						println("> Match started successfully!")
					}
				}
				"2" -> {
					println("> enter player name")
					val name = safeReadCommand()
					val profile = getPlayerProfile(name)
					if (profile != null) {
						println("> Stats: Wins: ${profile.gamesWon}, Losses: ${profile.gamesLost}")
					} else {
						println("> Not found")
					}
				}
				"3" -> {
					getLeaderBoard().forEach { p ->
						println("> ${p.playerName}: ${p.gamesWon} wins")
					}
				}
				"0" -> return
			}
		}
	}

	override fun getLeaderBoard(): List<PlayerStats> = gameManager.getLeaderboard()
	override fun getPlayerProfile(name: String): PlayerStats? = gameManager.getPlayerStats(name)
	override fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>? = gameManager.getMatchHistory(id)

	private fun safeReadCommand(): String = readLine()?.trim() ?: ""

	private fun startNewGame(): ManagerResult {
		gameManager.loginPlayer("Player 1")
		gameManager.loginPlayer("Player 2")
		return gameManager.startMatch()
	}
}
