package org.example.boardgame.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Players")
data class PlayerEntity(
    @PrimaryKey val name: String,
    val wins: Int = 0,
    val losses: Int = 0
)
