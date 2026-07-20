package org.example.boardgame.ui.screens.stats

import gamemanager.GameManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerStatsViewModel(
    private val gameManager: GameManager,
    private val coroutineScope: CoroutineScope
) {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<PlayerStatsUiState>(PlayerStatsUiState.Idle)
    val uiState: StateFlow<PlayerStatsUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _uiState.value = PlayerStatsUiState.Idle
        }
    }

    fun onSearchClicked() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return

        _uiState.value = PlayerStatsUiState.Loading

        coroutineScope.launch {
            try {
                val result = gameManager.getPlayerStats(query)
                if (result != null) {
                    _uiState.value = PlayerStatsUiState.Success(result)
                } else {
                    _uiState.value = PlayerStatsUiState.Error("Player '$query' not found")
                }
            } catch (e: Exception) {
                _uiState.value = PlayerStatsUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun onClearSearch() {
        _searchQuery.value = ""
        _uiState.value = PlayerStatsUiState.Idle
    }
}
