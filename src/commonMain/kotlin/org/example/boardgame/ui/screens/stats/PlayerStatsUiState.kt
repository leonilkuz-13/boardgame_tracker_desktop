package org.example.boardgame.ui.screens.stats

import common.PlayerStats

sealed class PlayerStatsUiState {
    data object Idle : PlayerStatsUiState()
    data object Loading : PlayerStatsUiState()
    data class Success(val stats: PlayerStats) : PlayerStatsUiState()
    data class Error(val message: String) : PlayerStatsUiState()
}
