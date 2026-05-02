package common

enum class ShipType(val maxCount: Int) {
    BATTLE_WAGON(1),
    CARRIER(1),
    CRUISER(3),
    DESTROYER(4),
    SUBMARINE(5),
    SPECIAL(1)
}
