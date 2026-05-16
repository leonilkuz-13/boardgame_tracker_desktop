package player

import board.Board

interface Player {
    val name : String // имя
    var radarCharges: Int // количество радаров
    var bomberCharges: Int // количество бомб
    val myBoard: Board // своя доска
    val enemyBoard: Board // чужая доска
    fun useRadar(): Boolean // валидация использования радара (boolean -- костыль для избавления try-catch выше, потому не вопрос)
    fun useBomber(): Boolean // валидация использования бомбардировщика
}