package org.example.boardgame.ui.screens.auth

data class LoginUiState(
    val player1Name: String = "",
    val player2Name: String = "",
    val player1Error: String? = null,
    val player2Error: String? = null,
    val isSuccess: Boolean = false
)
