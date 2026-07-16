package org.example.boardgame.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val player1_name: String,
    val player2_name: String,
    val winner_name: String
)
