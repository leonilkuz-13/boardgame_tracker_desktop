package common

import kotlin.math.max
import kotlin.math.abs

data class Coordinate(val x: Char, val y: Int) {

    override fun toString(): String {
        return "$x$y"
    }

    companion object {
        fun parse(line: String): Coordinate? {
            val cleanLine = line.trim().uppercase()
            if (cleanLine.length < 2) return null
            val x = cleanLine[0]
            val y = cleanLine.substring(1).toIntOrNull() ?: return null
            val coordinate = Coordinate(x, y)
            return if (coordinate.isValid()) coordinate else null
        }
    }

    // корректность передачи координат
    fun isValid(): Boolean {
        return (x in 'A'..'O' && y in 1..15)
    }

    // расстояние между точками метрикой Чебышева
    private fun distanceTo(other: Coordinate): Int {
        return max(abs(this.x - other.x), abs(this.y - other.y) )
    }

    // точка рядом
    fun dotAdjacentTo(other: Coordinate): Boolean {
        return this.distanceTo(other) == 1
    }

    // Возвращает все валидные клетки в радиусе 1, без точки от которой вызван метод
    fun getNeighbors(): List<Coordinate> {
        val neighbors = mutableListOf<Coordinate>()

        for (dx in -1..1) {
            for (dy in -1..1) {
                val neighbor = Coordinate(this.x + dx, this.y + dy)
                if (neighbor.isValid()) {
                    neighbors.add(neighbor)
                }
            }
        }
        neighbors.remove(Coordinate(this.x, this.y))
        return neighbors
    }
}