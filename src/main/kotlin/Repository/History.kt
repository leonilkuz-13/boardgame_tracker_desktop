package Repository

import common.Move
import common.MoveResult

interface History {
    fun saveMatch(playerName1: String, playerName2: String, winnerName: String, currentLog: List<Pair<Move, MoveResult>>) // Пакетное сохранение всего матча (и общей инфы, и всех ходов) разом
    fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? // Загрузка лога ходов конкретного матча для просмотра реплея
}