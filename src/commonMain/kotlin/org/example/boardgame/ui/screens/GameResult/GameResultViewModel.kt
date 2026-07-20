package org.example.boardgame.ui.screens.GameResult

import gamemanager.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameResultViewModel(
    private val gameManager: GameManager,
    private val winnerName: String
) {
    private val _uiState = MutableStateFlow(GameResultUiState())
    val uiState: StateFlow<GameResultUiState> = _uiState.asStateFlow()

    init {
        val myName = gameManager.getCurrentPlayerName()
        _uiState.value = GameResultUiState(
            winnerName = winnerName,
            isMyVictory = (winnerName == myName)
        )
    }

    fun onReturnToMenuClicked() {
        gameManager.abortMatch()
    }

    fun onWatchReplayClicked() {
        gameManager.abortMatch()
    }
}
