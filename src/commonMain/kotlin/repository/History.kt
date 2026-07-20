package repository

import common.Move
import common.MoveResult

data class MatchSummary(
    val id: Int,
    val player1Name: String,
    val player2Name: String,
    val winnerName: String
)

interface History {
    fun saveMatch(playerName1: String, playerName2: String, winnerName: String, currentLog: List<Pair<Move, MoveResult>>) // Пакетное сохранение всего матча (и общей инфы, и всех ходов) разом
    fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? // Загрузка лога ходов конкретного матча для просмотра реплея
    fun getMatchSummary(matchId: Int): MatchSummary?
}
