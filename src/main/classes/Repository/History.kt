package Repository

import game.Move
import game.Result

interface History {
    fun startNewSession(playerName: String): Int // создание новой партии и возвразения его ID
    fun recordMove(id: Int ,move: Move, result: Result) // запись выстрела
    fun getHistory(): List<Pair<Move, Result>> // получение истории выстрелов
}


