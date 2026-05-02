package Console

import player.Player

interface Console {
    fun createUser(name: String) : ???
    fun showLeaderboard(): List<???>
    fun showUserHistory(name: String): ???
    fun startNewSession(player1: Player, player2: Player): Int
    fun singleAttack(???): ???
    fun bomberAttack(???): ???
    fun scan(???):???
    fun surrender(???): ???
}