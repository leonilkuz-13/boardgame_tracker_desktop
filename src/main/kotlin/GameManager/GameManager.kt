package GameManager

import common.PlayerStats
import common.ManagerResult
import common.Move
import common.MoveResult


interface GameManager {
    fun loginPlayer(playerName: String): ManagerResult // синхронизация имени, введеное игроком с базой
    fun getLeaderboard(): List<PlayerStats> // лидерборд (прокид к view)
    fun getPlayerProfile(name: String): PlayerStats? // статистика профиля по имени (прокид к view)
    fun getMatchHistory(id: Int): List<Pair<Move, MoveResult>>?  // история партии по id из базы (прокид к view)
    fun handleMove(action: Move): MoveResult // переход по действию в game
    fun startMatch(): ManagerResult // вот именно тут будет происходить создание player, досок для него и тд
    fun startGame(): MoveResult.Error? // функция нужна для переключения состояния state в Game (контринтуитивно, но возвращает null, если все норм)
    fun getCurrentPlayerName() : String // получить имя игрока, который сейчас ходит (для отображения в интерфейсе)
    fun switchTurn() // переключать состояния при расстановке
    fun abortMatch() // прерывание матча
}