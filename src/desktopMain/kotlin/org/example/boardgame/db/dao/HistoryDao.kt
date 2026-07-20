package org.example.boardgame.db.dao

import org.example.boardgame.db.entities.MatchEntity
import org.example.boardgame.db.entities.MoveEntity
import java.sql.Connection

interface HistoryDao {
    fun insertMatch(connection: Connection, p1: String, p2: String, winner: String): Int
    fun insertMove(connection: Connection, matchId: Int, turn: Int, action: String, coords: String, result: String, shipType: String?)
    fun getMoves(matchId: Int): List<MoveEntity>
    fun getMatch(matchId: Int): MatchEntity?
}
