package org.example.boardgame.db.entities

data class PlayerEntity(
    val name: String,
    val wins: Int,
    val losses: Int
)
