package org.example.boardgame.db.entities

data class MoveEntity(
    val id: Int,
    val matchId: Int,
    val turnNumber: Int,
    val typeAction: String,
    val coordinates: String,
    val resultStatus: String,
    val typeShip: String?
)
