package org.example.boardgame.ui.screens.leaderboard

import common.PlayerStats
import gamemanager.GameManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val gameManager: GameManager,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        scope.launch {
            _uiState.value = LeaderboardUiState.Loading
            try {
                val players = gameManager.getLeaderboard()
                _uiState.value = LeaderboardUiState.Success(players, SortOption.WINS)
            } catch (e: Exception) {
                _uiState.value = LeaderboardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onSortOptionSelected(option: SortOption) {
        val currentState = _uiState.value
        if (currentState is LeaderboardUiState.Success) {
            val sortedList = when (option) {
                SortOption.WINS -> currentState.players.sortedByDescending { it.gamesWon }
                SortOption.WIN_RATE -> currentState.players.sortedByDescending { it.winRate }
                SortOption.TOTAL_GAMES -> currentState.players.sortedByDescending { it.gamesPlayed }
            }
            _uiState.value = currentState.copy(players = sortedList, currentSort = option)
        }
    }
}
