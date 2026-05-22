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
import java.sql.Statement

class HistoryImpl: History {
    override fun saveMatch(
        playerName1: String,
        playerName2: String,
        winnerName: String,
        currentLog: List<Pair<Move, MoveResult>>
    ) {
        val insertMatchQuery = "INSERT INTO MATCHES (player1_name, player2_name, winner_name) VALUES (?, ?, ?)"
        val insertMovesQuery = "INSERT INTO Moves (match_id, turn_number, type_action, coordinates, result_status, type_ship) VALUES (?, ?, ?, ?, ?, ?)"

        DatabaseManager.getConnection().use { connection ->
            try {
                connection.autoCommit = false // флаг фалс переводит базу в транзакционный режим

                val statement = connection.prepareStatement(
                    insertMatchQuery,
                    Statement.RETURN_GENERATED_KEYS
                ) // prepareStatement делает шаблон для запроса.
                statement.setString(1, playerName1) // заполнение вопросиков в запросе
                statement.setString(2, playerName2)
                statement.setString(3, winnerName)
                statement.executeUpdate()

                var currentMatchId = -1
                val key = statement.generatedKeys
                if (key.next()) { // стоим на заголовке, если строка появилась, то смотрим на первую колонку
                    currentMatchId = key.getInt(1)
                }

                val moveStatement = connection.prepareStatement(insertMovesQuery)
                currentLog.forEachIndexed { index, pair ->
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

                    moveStatement.setInt(1, currentMatchId)
                    moveStatement.setInt(2, index + 1)
                    moveStatement.setString(3, move.javaClass.simpleName)
                    moveStatement.setString(4, coordinatesString)
                    moveStatement.setString(5, result.javaClass.simpleName)
                    moveStatement.setString(6,shipTypeToSave)
                    moveStatement.executeUpdate()
                }
                connection.commit()
            } catch (e: Exception) {
                connection.rollback()
                println("> Save error: ${e.message}. All changes are canceled")
                throw e
            } finally {
                connection.autoCommit = true
            }
        }
    }

    override fun getMatchReplay(matchId: Int): List<Pair<Move, MoveResult>>? {
        val replayLog = mutableListOf<Pair<Move, MoveResult>>()

        val selectQuery = "SELECT type_action, coordinates, result_status, type_ship FROM MOVES WHERE match_id = ? ORDER BY turn_number ASC" // выбираем из колонки moves, предварительно отсортировав ее по возрастанию

        DatabaseManager.getConnection().use { connection ->
            val statement = connection.prepareStatement(selectQuery)
            statement.setInt(1, matchId)

            val results = statement.executeQuery()
            while (results.next()) {
                val actionType = results.getString("type_action")
                val coordinate = results.getString("coordinates")
                val result = results.getString("result_status")
                val typeShipString = results.getString("type_ship")

                val parsedShipType: ShipType? = if (typeShipString != null) {
                    ShipType.valueOf(typeShipString)
                } else {
                    null
                }
                val move = parseMove(actionType, coordinate, parsedShipType)
                val moveResult = parseResult(result, coordinate, parsedShipType)

                if (moveResult == null || move == null) {
                    return null

                }

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
                val ship: Ship = when(parsedShipType) {
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
            "Sunk" -> MoveResult.Success.Sunk(coordinate!!, emptySet(), parsedShipType) // я в affectedCoordinates прокинул пустой список, потому что активные координаты вокруг не нужны в реплее, по моему мнению
            "Over" -> MoveResult.Success.Over(coordinate!!, emptySet(), parsedShipType)
            "ScanResult" -> MoveResult.ScanResult(emptyMap())
            "GrandResult" -> MoveResult.GrandResult(emptyList())
            "ShipInstall" -> {
                val allCoordinates = coordinates.split(", ").mapNotNull { Coordinate.parse(it) }
                MoveResult.ShipInstall(allCoordinates, emptySet(), parsedShipType) // аналогично over
            }
            else -> null
        }
    }
}