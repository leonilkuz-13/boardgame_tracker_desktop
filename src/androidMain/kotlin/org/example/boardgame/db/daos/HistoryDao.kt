package org.example.boardgame.db.daos

import androidx.room.*
import org.example.boardgame.db.entities.MatchEntity
import org.example.boardgame.db.entities.MoveEntity

@Dao
interface HistoryDao {
    @Insert
    fun insertMatch(match: MatchEntity): Long

    @Insert
    fun insertMove(move: MoveEntity)

    @Insert
    fun insertMoves(moves: List<MoveEntity>)

    @Transaction
    fun saveMatchWithMoves(match: MatchEntity, moves: List<MoveEntity>) {
        val matchId = insertMatch(match).toInt()
        val movesWithId = moves.map { it.copy(match_id = matchId) }
        insertMoves(movesWithId)
    }

    @Query("SELECT * FROM Moves WHERE match_id = :matchId ORDER BY turn_number ASC")
    fun getMovesByMatchId(matchId: Int): List<MoveEntity>
}
