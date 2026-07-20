package org.example.boardgame.db.impl

import battleship.*
import common.*
import org.example.boardgame.db.daos.HistoryDao
import org.example.boardgame.db.entities.MatchEntity
import org.example.boardgame.db.entities.MoveEntity
import repository.History
import repository.MatchSummary

class AndroidHistoryImpl(private val historyDao: HistoryDao) : History {

    override fun saveMatch(
        playerName1: String,
        playerName2: String,
        winnerName: String,
        currentLog: List<Pair<Move, MoveResult>>
    ) {
        val match = MatchEntity(
            player1_name = playerName1,
            player2_name = playerName2,
            winner_name = winnerName
        )

        val moveEntities = currentLog.mapIndexed { index, (move, result) ->
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

            MoveEntity(
                match_id = 0, // Will be set in transaction
                turn_number = index + 1,
                type_action = move::class.simpleName ?: "Unknown",
                coordinates = coordinatesString,
                result_status = result::class.simpleName ?: "Unknown",
                type_ship = shipTypeToSave
            )
        }

        historyDao.saveMatchWithMoves(match, moveEntities)
    }

    override fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? {
        val entities = historyDao.getMovesByMatchId(matchId)
        if (entities.isEmpty()) return null

        return entities.mapNotNull { entity ->
            val parsedShipType = entity.type_ship?.let { ShipType.valueOf(it) }
            val move = parseMove(entity.type_action, entity.coordinates, parsedShipType)
            val result = parseResult(entity.result_status, entity.coordinates, parsedShipType)
            if (move != null && result != null) Pair(move, result) else null
        }
    }

    override fun getMatchSummary(matchId: Int): MatchSummary? {
        val entity = historyDao.getMatchById(matchId) ?: return null
        return MatchSummary(
            id = entity.id,
            player1Name = entity.player1_name,
            player2Name = entity.player2_name,
            winnerName = entity.winner_name
        )
    }

    private fun parseMove(actionType: String, coordinatesStr: String, parsedShipType: ShipType?): Move? {
        return when (actionType.lowercase()) {
            "singleattack" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.SingleAttack(coordinate) else null
            }
            "grandattack" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.GrandAttack(coordinate) else null
            }
            "radar" -> {
                val coordinate = Coordinate.parse(coordinatesStr)
                if (coordinate != null) Move.Radar(coordinate) else null
            }
            "install" -> {
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

        if (coordinate == null && result.lowercase() != "shipinstall") return null

        return when (result.lowercase()) {
            "hit" -> MoveResult.Success.Hit(coordinate!!)
            "miss" -> MoveResult.Success.Miss(coordinate!!)
            "sunk" -> MoveResult.Success.Sunk(coordinate!!, emptySet(), parsedShipType)
            "over" -> MoveResult.Success.Over(coordinate!!, emptySet(), parsedShipType)
            "scanresult" -> MoveResult.ScanResult(emptyMap())
            "grandresult" -> MoveResult.GrandResult(emptyList())
            "shipinstall" -> {
                val allCoordinates = coordinates.split(", ").mapNotNull { Coordinate.parse(it) }
                MoveResult.ShipInstall(allCoordinates, emptySet(), parsedShipType)
            }
            else -> null
        }
    }
}
