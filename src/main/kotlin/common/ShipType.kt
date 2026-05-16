package common

// определяют тип корабля
enum class ShipType(val maxCount: Int, val size: Int) {
    BATTLE_WAGON(1, 5),
    CARRIER(1, 4),
    CRUISER(3, 3),
    DESTROYER(4, 2),
    SUBMARINE(5, 1),
    SPECIAL(1, 4);
}
