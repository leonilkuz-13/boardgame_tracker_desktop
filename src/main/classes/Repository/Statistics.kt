package Repository

interface Statistics {
    fun getPlayerStats(playerName: String): PlayerStats? // статистика по игроку
    fun getTopPlayers(limit: Int): List<PlayerStats> // топ-10 игроков
    fun saveMatchResult(playerName: String, isWin: Boolean) // сохарнение результатов после окончания партии
    fun createPlayer(playerName: String) // Запись в БД самого факта существования нового игрока
}