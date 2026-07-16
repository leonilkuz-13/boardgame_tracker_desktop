package org.example.boardgame.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Moves")
data class MoveEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val match_id: Int,
    val turn_number: Int,
    val type_action: String,
    val coordinates: String,
    val result_status: String,
    val type_ship: String?
)
