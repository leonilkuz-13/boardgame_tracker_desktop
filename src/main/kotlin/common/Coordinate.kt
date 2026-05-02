package common

import kotlin.math.max
import kotlin.math.abs

data class Coordinate(val x: Int, val y: Int) {

    // корректность передачи координат
    fun isValid(): Boolean {
        return (x in 0..14 && y in 0..14)
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