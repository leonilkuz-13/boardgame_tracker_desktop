package GameManager

import Repository.PlayerStats
import common.Move
import common.MoveResult
import game.Result

interface GameManager {
    fun loginPlayer(playerName: String) // плеер заходит в базу
    fun getLeaderboard(): List<PlayerStats> // получить список лидеров
    fun getPlayerProfile(name: String): PlayerStats? // получение статистики профиля игрока
    fun getMatchHistory(): List<Pair<Move, MoveResult>>  // история матчей
    fun handleMove(action: Move) // сделать действие
    fun StartMatch() // вот именно тут будет происходить создание player, досок для него и тд
}