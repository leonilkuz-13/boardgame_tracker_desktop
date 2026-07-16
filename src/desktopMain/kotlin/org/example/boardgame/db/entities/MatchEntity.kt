package org.example.boardgame.db.entities

data class MatchEntity(
    val id: Int,
    val player1Name: String,
    val player2Name: String,
    val winnerName: String
)
