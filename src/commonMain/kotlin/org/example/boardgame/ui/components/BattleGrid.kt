package org.example.boardgame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import battleship.Ship
import common.CellStatus
import common.Coordinate
import org.example.boardgame.ui.theme.PencilDark

@Composable
fun BattleGrid(
    getStatus: (Coordinate) -> CellStatus,
    onCellClick: (Coordinate) -> Unit,
    modifier: Modifier = Modifier,
    ships: List<Ship> = emptyList()
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = 15

        val side = if (this.maxWidth < this.maxHeight) this.maxWidth else this.maxHeight
        val cellSize = side / (boardSize + 1)
        val totalGridSize = cellSize * (boardSize + 1)

        Box(modifier = Modifier.requiredSize(totalGridSize)) {
            // --- 1. WHITE BACKGROUND FOR THE GRID AREA ---
            Box(
                modifier = Modifier
                    .offset(x = cellSize, y = cellSize)
                    .size(cellSize * boardSize)
                    .background(Color.White)
            )

            // --- 2. LABELS (A-O, 1-15) ---
            ('A' until 'A' + boardSize).forEachIndexed { i, char ->
                Box(
                    modifier = Modifier
                        .requiredSize(cellSize)
                        .offset(x = cellSize * (i + 1), y = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char.toString(),
                        fontSize = (cellSize.value * 0.5f).sp,
                        color = PencilDark,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            (1..boardSize).forEach { y ->
                Box(
                    modifier = Modifier
                        .requiredSize(cellSize)
                        .offset(x = 0.dp, y = cellSize * y),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = y.toString(),
                        fontSize = (cellSize.value * 0.45f).sp,
                        color = PencilDark,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // --- 3. CELLS (Transparent, but handle interaction) ---
            for (y in 1..boardSize) {
                for (i in 0 until boardSize) {
                    val xChar = ('A'.code + i).toChar()
                    val coord = Coordinate(xChar, y)

                    BattleCell(
                        status = getStatus(coord),
                        onClick = { onCellClick(coord) },
                        modifier = Modifier.offset(x = cellSize * (i + 1), y = cellSize * y),
                        cellSize = cellSize
                    )
                }
            }

            // --- 4. GRID LINES (Drawn ON TOP of cells for visibility) ---
            Canvas(
                modifier = Modifier
                    .offset(x = cellSize, y = cellSize)
                    .size(cellSize * boardSize)
            ) {
                val gridSide = size.width
                val step = gridSide / boardSize
                val color = PencilDark.copy(alpha = 0.3f) // More visible gray
                val strokeWidth = 1.dp.toPx()

                for (i in 0..boardSize) {
                    val pos = i * step
                    // Vertical
                    drawLine(
                        color = color,
                        start = Offset(pos, 0f),
                        end = Offset(pos, gridSide),
                        strokeWidth = strokeWidth
                    )
                    // Horizontal
                    drawLine(
                        color = color,
                        start = Offset(0f, pos),
                        end = Offset(gridSide, pos),
                        strokeWidth = strokeWidth
                    )
                }
            }

            // --- 5. SHIPS ---
            ships.forEach { ship ->
                if (ship.coordinates.isNotEmpty()) {
                    val minXChar = ship.coordinates.minOf { it.x }
                    val minY = ship.coordinates.minOf { it.y }
                    val maxXChar = ship.coordinates.maxOf { it.x }
                    val maxY = ship.coordinates.maxOf { it.y }

                    val startX = cellSize * (minXChar - 'A' + 1)
                    val startY = cellSize * minY

                    val width = cellSize * (maxXChar - minXChar + 1)
                    val height = cellSize * (maxY - minY + 1)

                    ShipRenderer(ship, startX, startY, width, height)
                }
            }
        }
    }
}
