package org.example.boardgame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import common.CellStatus
import org.example.boardgame.ui.theme.*

@Composable
fun BattleCell(
    status: CellStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
    cellSize: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .requiredSize(cellSize)
            .clipToBounds()
            .then(
                if (isClickable && (status == CellStatus.EMPTY || status == CellStatus.SHIP))
                    Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 3.dp.toPx()
            val padding = size.width * 0.25f

            when (status) {
                // Корабли мы рисуем отдельным слоем поверх всей доски, поэтому здесь ячейка остается пустой
                CellStatus.EMPTY, CellStatus.SHIP -> {}

                CellStatus.HIT -> {
                    drawLine(
                        color = PencilRed,
                        start = Offset(padding, padding),
                        end = Offset(size.width - padding, size.height - padding),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = PencilRed,
                        start = Offset(size.width - padding, padding),
                        end = Offset(padding, size.height - padding),
                        strokeWidth = strokeWidth
                    )
                }
                CellStatus.MISS -> {
                    drawCircle(
                        color = PenBlue,
                        radius = size.width / 10f,
                        center = center
                    )
                }
                CellStatus.BORDER -> {
                    val borderColor = PencilLight.copy(alpha = 0.15f)
                    val hatchSpacing = 8.dp.toPx()
                    val hatchWidth = 1.dp.toPx()
                    val w = size.width
                    val h = size.height
                    var t = hatchSpacing

                    while (t < w + h) {
                        val x1 = if (t <= h) 0f else t - h
                        val y1 = if (t <= h) t else h
                        val x2 = if (t <= w) t else w
                        val y2 = if (t <= w) 0f else t - w
                        drawLine(borderColor, Offset(x1, y1), Offset(x2, y2), hatchWidth)
                        t += hatchSpacing
                    }
                }
            }
        }
    }
}