package common

enum class GameState {
    SETUP,   // Фаза расстановки кораблей
    COMBAT,  // Фаза боя (стрельба, радары)
    FINISHED // Игра окончена
}