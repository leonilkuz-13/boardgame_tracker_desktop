package player

import board.Board

interface Player {
    val name : String // имя
    var radarCharges: Int // количество радаров
    var bomberCharges: Int // количество бомб
    val myBoard: Board // своя доска
    val enemyBoard: Board // чужая доска
    fun isUseRadar(): Unit // валидация использования радара
    fun isUseBomber(): Unit // валидация использования бомбардировщика
}