import common.Coordinate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CoordinateTest {

    @Nested
    @DisplayName("Тесты функции isValid")
    inner class ValidityTests {

        @ParameterizedTest
        @CsvSource(
            "0, 0",   // Левый нижний угол
            "14, 14", // Правый верхний угол
            "7, 7"    // Центр поля
        )
        fun `should return true for coordinates within 0-14`(x: Int, y: Int) {
            assertTrue(Coordinate(x, y).isValid())
        }

        @ParameterizedTest
        @CsvSource(
            "-1, 5",  // X за пределами (лево)
            "15, 5",  // X за пределами (право)
            "5, -1",  // Y за пределами (низ)
            "5, 15"   // Y за пределами (верх)
        )
        fun `should return false for coordinates outside 0-14`(x: Int, y: Int) {
            assertFalse(Coordinate(x, y).isValid())
        }
    }

    @Nested
    @DisplayName("Тесты функции dotAdjacentTo")
    inner class AdjacencyTests {

        @Test
        fun `should return true for adjacent dots (Chebyshev distance 1)`() {
            val center = Coordinate(5, 5)

            // Проверяем все 8 направлений вокруг точки
            assertTrue(center.dotAdjacentTo(Coordinate(4, 4)), "Diagonal LD")
            assertTrue(center.dotAdjacentTo(Coordinate(5, 4)), "Down")
            assertTrue(center.dotAdjacentTo(Coordinate(6, 6)), "Diagonal RU")
            assertTrue(center.dotAdjacentTo(Coordinate(6, 5)), "Right")
        }

        @Test
        fun `should return false if dots are not adjacent`() {
            val center = Coordinate(5, 5)

            assertFalse(center.dotAdjacentTo(Coordinate(5, 5)), "Same point is not adjacent")
            assertFalse(center.dotAdjacentTo(Coordinate(5, 7)), "Distance is 2 (Vertical)")
            assertFalse(center.dotAdjacentTo(Coordinate(3, 5)), "Distance is 2 (Horizontal)")
        }
    }
}