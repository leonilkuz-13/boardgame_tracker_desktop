package org.example.boardgame.ui.screens.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import gamemanager.GameManager

class HistorySearchViewModel(private val gameManager: GameManager) {
    var matchId by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var isSearchSuccessful by mutableStateOf(false)

    fun onSearch() {
        val id = matchId.toIntOrNull()
        if (id == null) {
            error = "Please enter a valid numeric ID"
            return
        }
        
        val history = gameManager.getMatchHistory(id)
        if (history == null || history.isEmpty()) {
            error = "Match with ID $id not found or has no history"
        } else {
            error = null
            isSearchSuccessful = true
        }
    }
}
