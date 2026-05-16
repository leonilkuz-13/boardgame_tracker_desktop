package common

sealed class MoveResult {

    data class ShipInstall(
        val shipCoordinates: List<Coordinate>, // Чтобы GUI нарисовал палубы
        val borderCoordinates: Set<Coordinate>, // Чтобы GUI закрасил нельзя ставить
        val shipType: ShipType? = null // необходимо для базы
    ) : MoveResult()

    data class ScanResult(val info: Map<Coordinate, CellStatus>) : MoveResult()
    data class GrandResult(val results: List<Success>) : MoveResult()

    sealed class Success : MoveResult() {
        abstract val coordinate: Coordinate

        data class Miss(override val coordinate: Coordinate) : Success()
        data class Hit(override val coordinate: Coordinate) : Success()
        data class Sunk(override val coordinate: Coordinate, val affectedCoordinates: Set<Coordinate>, val shipType: ShipType? = null) : Success()
        data class Over(override val coordinate: Coordinate, val affectedCoordinates: Set<Coordinate>, val shipType: ShipType? = null) : Success()
    }

    sealed class Error : MoveResult() {
        abstract val reason: String
        data class GameError(override val reason: String) : Error() // ошибка в самой игре (общая для установки, ударов, сканирования)

        data class InvalidMove(override val reason: String) : Error()
    }
}