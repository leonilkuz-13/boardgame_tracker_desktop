package board

import common.CellStatus
import battleship.Ship
import common.Coordinate

interface Board {
    fun getCellStatus(coordinate: Coordinate): CellStatus // узнавать состояние координаты
    fun updateCellStatus(coordinate: Coordinate, newStatus: CellStatus) // это менять состояние в движках
    fun getSnapshot(): Map<Coordinate, CellStatus> // получение слепка всей доски
    fun isWithinBounds(coordinate: Coordinate): Boolean // проверка на валидность координаты
    fun hasShip(coordinate: Coordinate): Boolean // есть ли корабль ?
    fun getShips(): List<Ship> // получаем список кораблей
    fun addShip(ship: Ship) // добавление корабля на доску
    fun getShipAt(coordinate: Coordinate): Ship? // получение корабля по координате
    fun hasAliveShips(): Boolean // есть ли живые корабли?
}