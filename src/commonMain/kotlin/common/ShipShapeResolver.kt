package common

/**
 * Common ship shapes and their relative offsets.
 * (0,0) is considered the "origin" cell where the user clicks.
 */
val shapeOffsets: Map<SpecialShape, List<Pair<Int, Int>>> = mapOf(
    SpecialShape.TOP_LEFT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 2)),
    SpecialShape.TOP_RIGHT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 2)),
    SpecialShape.MIDDLE_LEFT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 1)),
    SpecialShape.MIDDLE_RIGHT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 1)),
    SpecialShape.BOTTOM_LEFT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 0)),
    SpecialShape.BOTTOM_RIGHT to listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 0)),
)

fun resolveSpecialShape(coordinates: List<Coordinate>): SpecialShape? {
    if (coordinates.size != 4) return null

    // Find min coordinates to normalize
    val minX = coordinates.minOf { it.x.code }
    val minY = coordinates.minOf { it.y }
    
    val normalizedActual = coordinates.map { 
        Pair(it.x.code - minX, it.y - minY) 
    }.toSet()

    for ((shape, offsets) in shapeOffsets) {
        val sMinX = offsets.minOf { it.first }
        val sMinY = offsets.minOf { it.second }
        val normalizedExpected = offsets.map { 
            Pair(it.first - sMinX, it.second - sMinY) 
        }.toSet()
        
        if (normalizedActual == normalizedExpected) return shape
    }
    return null
}
