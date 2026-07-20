package org.example.boardgame.repository

import battleship.*
import common.*
import repository.History
import repository.MatchSummary
import org.example.boardgame.db.dao.HistoryDao
import org.example.boardgame.db.entities.MoveEntity

class HistoryImpl(private val historyDao: HistoryDao) : History {
    
    override fun saveMatch(
        playerName1: String,
        playerName2: String,
        winnerName: String,
        currentLog: List<Pair<Move, MoveResult>>
    ) {
        DatabaseManager.getConnection().use { connection ->
            try {
                connection.autoCommit = false
                
                val matchId = historyDao.insertMatch(connection, playerName1, playerName2, winnerName)
                if (matchId == -1) {
                    connection.rollback()
                    return
                }

                currentLog.forEachIndexed { index, (move, result) ->
                    val coordinatesString = when (move) {
                        is Move.SingleAttack -> move.coordinate.toString()
                        is Move.GrandAttack -> move.center.toString()
                        is Move.Radar -> move.center.toString()
                        is Move.Install -> move.coordinates.joinToString(separator = ", ")
                    }

                    var shipTypeToSave: String? = null
                    when (result) {
                        is MoveResult.ShipInstall -> shipTypeToSave = result.shipType?.name
                        is MoveResult.Success.Over -> shipTypeToSave = result.shipType?.name
                        is MoveResult.Success.Sunk -> shipTypeToSave = result.shipType?.name
                        else -> Unit
                    }

                    historyDao.insertMove(
                        connection,
                        matchId,
                        index + 1,
                        move::class.simpleName ?: "Unknown",
                        coordinatesString,
                        result::class.simpleName ?: "Unknown",
                        shipTypeToSave
                    )
                }
                
                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                println("> Save error: ${e.message}")
            } finally {
                connection.autoCommit = true
            }
        }
    }

    override fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? {
        val moveEntities = historyDao.getMoves(matchId)
        
        if (moveEntities.isEmpty()) return null

        val replayLog = mutableListOf<Pair<Move, MoveResult>>()
        
        moveEntities.forEach { entity ->
            val parsedShipType = entity.typeShip?.let { ShipType.valueOf(it) }
            
            val move = parseMove(entity.typeAction, entity.coordinates, parsedShipType)
            val moveResult = parseResult(entity.resultStatus, entity.coordinates, parsedShipType)
            
            if (move != null && moveResult != null) {
                replayLog.add(Pair(move, moveResult))
            }
        }
        
        return if (replayLog.isEmpty()) null else replayLog
    }

    override fun getMatchSummary(matchId: Int): MatchSummary? {
        val entity = historyDao.getMatch(matchId) ?: return null
        return MatchSummary(
            id = entity.id,
            player1Name = entity.player1Name,
            player2Name = entity.player2Name,
            winnerName = entity.winnerName
        )
    }

    private fun parseMove(actionType: String, coordinatesStr: String, parsedShipType: ShipType?): Move? {
        return when (actionType) {
            "SingleAttack" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.SingleAttack(coordinate) else null
            }
            "GrandAttack" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.GrandAttack(coordinate) else null
            }
            "Radar" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.Radar(coordinate) else null
            }
            "Install" -> {
                val coordinates = coordinatesStr.split(", ").mapNotNull { Coordinate.parse(it) }
                if (parsedShipType == null) return null
                val ship: Ship = when(parsedShipType) {
                    ShipType.BATTLE_WAGON -> BattleWagon(coordinates)
                    ShipType.CARRIER -> Carrier(coordinates)
                    ShipType.CRUISER -> Cruiser(coordinates)
                    ShipType.DESTROYER -> Destroyer(coordinates)
                    ShipType.SUBMARINE -> Submarine(coordinates)
                    ShipType.SPECIAL -> SpecialShip(coordinates, resolveSpecialShape(coordinates))
                }
                Move.Install(ship, coordinates)
            }
            else -> null
        }
    }

    private fun parseResult(result: String, coordinates: String, parsedShipType: ShipType?): MoveResult? {
        val firstCoordinate = coordinates.split(", ").firstOrNull() ?: ""
        val coordinate = Coordinate.parse(firstCoordinate)

        if (coordinate == null && result != "ShipInstall") return null

        return when (result) {
            "Hit" -> MoveResult.Success.Hit(coordinate!!)
            "Miss" -> MoveResult.Success.Miss(coordinate!!)
            "Sunk" -> MoveResult.Success.Sunk(coordinate!!, emptySet(), parsedShipType)
            "Over" -> MoveResult.Success.Over(coordinate!!, emptySet(), parsedShipType)
            "ScanResult" -> MoveResult.ScanResult(emptyMap())
            "GrandResult" -> MoveResult.GrandResult(emptyList())
            "ShipInstall" -> {
                val allCoordinates = coordinates.split(", ").mapNotNull { Coordinate.parse(it) }
                MoveResult.ShipInstall(allCoordinates, emptySet(), parsedShipType)
            }
            else -> null
        }
    }
}
