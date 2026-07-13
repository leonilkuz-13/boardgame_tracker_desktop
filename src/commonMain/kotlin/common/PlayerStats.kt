package common

data class PlayerStats(
    val playerName: String,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val winRate: Int,
    val gamesLost: Int
)