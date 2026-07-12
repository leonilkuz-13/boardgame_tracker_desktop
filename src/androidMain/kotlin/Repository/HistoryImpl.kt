package Repository

import battleship.BattleWagon
import battleship.Carrier
import battleship.Cruiser
import battleship.Destroyer
import battleship.Ship
import battleship.SpecialShip
import battleship.Submarine
import common.Coordinate
import common.Move
import common.MoveResult
import common.ShipType

class HistoryImpl(context: android.content.Context) : History {

    private val dbHelper = DatabaseHelper(context)

    override fun saveMatch(
        playerName1: String,
        playerName2: String,
        winnerName: String,
        currentLog: List<Pair<Move, MoveResult>>
    ) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cv = android.content.ContentValues().apply {
                put("player1_name", playerName1)
                put("player2_name", playerName2)
                put("winner_name", winnerName)
            }
            val matchId = db.insert("Matches", null, cv)

            for ((index, pair) in currentLog.withIndex()) {
                val move = pair.first
                val result = pair.second

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

                val moveCv = android.content.ContentValues().apply {
                    put("match_id", matchId)
                    put("turn_number", index + 1)
                    put("type_action", move.javaClass.simpleName)
                    put("coordinates", coordinatesString)
                    put("result_status", result.javaClass.simpleName)
                    put("type_ship", shipTypeToSave)
                }
                db.insert("Moves", null, moveCv)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw e
        } finally {
            db.endTransaction()
        }
    }

    override fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? {
        val replayLog = mutableListOf<Pair<Move, MoveResult>>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT type_action, coordinates, result_status, type_ship FROM MOVES WHERE match_id = ? ORDER BY turn_number ASC",
            arrayOf(matchId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val actionType = it.getString(0)
                val coordinate = it.getString(1)
                val result = it.getString(2)
                val typeShipString = it.getString(3)

                val parsedShipType: ShipType? = if (typeShipString != null) {
                    try { ShipType.valueOf(typeShipString) } catch (_: IllegalArgumentException) { null }
                } else null

                val move = parseMove(actionType, coordinate, parsedShipType)
                val moveResult = parseResult(result, coordinate, parsedShipType)

                if (moveResult == null || move == null) return null
                replayLog.add(Pair(move, moveResult))
            }
        }
        return replayLog
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
                val ship: Ship = when (parsedShipType) {
                    ShipType.BATTLE_WAGON -> BattleWagon(coordinates)
                    ShipType.CARRIER -> Carrier(coordinates)
                    ShipType.CRUISER -> Cruiser(coordinates)
                    ShipType.DESTROYER -> Destroyer(coordinates)
                    ShipType.SUBMARINE -> Submarine(coordinates)
                    ShipType.SPECIAL -> SpecialShip(coordinates)
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
