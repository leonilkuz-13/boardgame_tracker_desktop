package org.example.boardgame.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.boardgame.db.daos.HistoryDao
import org.example.boardgame.db.daos.PlayerDao
import org.example.boardgame.db.entities.MatchEntity
import org.example.boardgame.db.entities.MoveEntity
import org.example.boardgame.db.entities.PlayerEntity

@Database(entities = [PlayerEntity::class, MatchEntity::class, MoveEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun historyDao(): HistoryDao
}
