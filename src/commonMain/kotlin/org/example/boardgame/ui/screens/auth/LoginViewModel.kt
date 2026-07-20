package org.example.boardgame.ui.screens.auth

import gamemanager.GameManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import common.ManagerResult

class LoginViewModel(
    private val gameManager: GameManager
) {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPlayer1NameChange(name: String) {
        _uiState.update { it.copy(player1Name = name, player1Error = null) }
    }

    fun onPlayer2NameChange(name: String) {
        _uiState.update { it.copy(player2Name = name, player2Error = null) }
    }

    fun onStartGame() {
        val p1 = _uiState.value.player1Name.trim()
        val p2 = _uiState.value.player2Name.trim()

        if (p1.isEmpty()) {
            _uiState.update { it.copy(player1Error = "Name cannot be empty") }
            return
        }
        if (p2.isEmpty()) {
            _uiState.update { it.copy(player2Error = "Name cannot be empty") }
            return
        }
        if (p1 == p2) {
            _uiState.update { it.copy(player2Error = "Names must be different") }
            return
        }

        gameManager.abortMatch()
        
        val res1 = gameManager.loginPlayer(p1)
        if (res1 is ManagerResult.Failure) {
            _uiState.update { it.copy(player1Error = res1.message) }
            return
        }

        val res2 = gameManager.loginPlayer(p2)
        if (res2 is ManagerResult.Failure) {
            _uiState.update { it.copy(player2Error = res2.message) }
            return
        }

        gameManager.startMatch()
        _uiState.update { it.copy(isSuccess = true) }
    }
}
