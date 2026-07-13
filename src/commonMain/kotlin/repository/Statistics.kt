package repository

import common.PlayerStats

interface Statistics {
    fun getPlayerStats(playerName: String): PlayerStats? // получение статистики игрока по имени
    fun getTopPlayers(limit: Int): List<PlayerStats> // получение топ 10 игроков для лидерборда
    fun saveMatchResult(playerName: String, isWin: Boolean) // Обновление счетчика побед/поражений после окончания игры
    fun createPlayer(playerName: String) // Регистрация нового имени в базе
}
