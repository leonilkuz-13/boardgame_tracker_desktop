package battleship

import common.ShipType
import common.Coordinate
import common.SpecialShape

interface Ship {
    val coordinates: List<Coordinate> // координаты корабля
    val type: ShipType // тип корабля
    val shape: SpecialShape? // форма спец-корабля
    fun receiveHit(coordinate: Coordinate) // обновление в спике корабля статуса клетки
    fun isSunk(): Boolean // проверка: жив\не
}
