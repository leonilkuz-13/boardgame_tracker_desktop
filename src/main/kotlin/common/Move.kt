package common

import battleship.Ship

sealed class Move {
    data class Install(val ship: Ship, val coordinates: List<Coordinate>) : Move()
    data class SingleAttack(val coordinate: Coordinate) : Move()
    data class GrandAttack(val coordinates: List<Coordinate>) : Move()
    data class Radar(val coordinates: List<Coordinate>) : Move()
}