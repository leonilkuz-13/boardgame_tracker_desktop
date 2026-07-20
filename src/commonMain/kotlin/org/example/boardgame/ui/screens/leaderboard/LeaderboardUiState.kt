package org.example.boardgame.ui.screens.leaderboard

import common.PlayerStats

sealed class LeaderboardUiState {
    data object Loading : LeaderboardUiState()
    data class Success(val players: List<PlayerStats>, val currentSort: SortOption) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}
