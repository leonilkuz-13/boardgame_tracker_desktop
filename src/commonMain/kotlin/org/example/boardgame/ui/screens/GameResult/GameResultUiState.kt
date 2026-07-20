package org.example.boardgame.ui.screens.GameResult

data class GameResultUiState(
    val winnerName: String = "",
    val isMyVictory: Boolean = false,
    val totalTurns: Int = 0
)
