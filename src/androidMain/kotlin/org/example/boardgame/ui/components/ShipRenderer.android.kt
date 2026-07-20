package org.example.boardgame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import battleship.Ship
import common.ShipType
import common.SpecialShape
import org.example.boardgame.R

@Composable
actual fun ShipRenderer(
    ship: Ship,
    offsetX: Dp,
    offsetY: Dp,
    drawWidth: Dp,
    drawHeight: Dp
) {
    val resId = when (ship.type) {
        ShipType.SUBMARINE -> R.drawable.ship_1
        ShipType.DESTROYER -> R.drawable.ship_22
        ShipType.CRUISER -> R.drawable.ship_3
        ShipType.CARRIER -> R.drawable.ship_4
        ShipType.BATTLE_WAGON -> R.drawable.ship_5
        ShipType.SPECIAL -> when (ship.shape) {
            SpecialShape.TOP_LEFT -> R.drawable.t_lev_ship_5
            SpecialShape.TOP_RIGHT -> R.drawable.t_right_ship_5
            SpecialShape.MIDDLE_LEFT -> R.drawable.j_left_ship_5
            SpecialShape.MIDDLE_RIGHT -> R.drawable.j_right_ship_5
            SpecialShape.BOTTOM_LEFT -> R.drawable.bot_lev_ship_5_1
            SpecialShape.BOTTOM_RIGHT -> R.drawable.bot_right_ship_5_1
            null -> R.drawable.ship_4
        }
    }

    val isVertical = drawHeight > drawWidth && ship.type != ShipType.SPECIAL

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            // ИСПРАВЛЕНИЕ 1: Жесткая фиксация общего контейнера корабля
            .requiredSize(drawWidth, drawHeight)
        // ТЕСТОВЫЙ ФОН: раскомментируй строку ниже, чтобы проверить свои PNG на "пустые края"
        // .background(Color.Red.copy(alpha = 0.5f))
        ,
        // ИСПРАВЛЕНИЕ 2: Ставим по центру! Это магия, которая позволяет вращать картинку идеально
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = if (isVertical) {
                Modifier
                    // ИСПРАВЛЕНИЕ 3: Изображение рисуем горизонтальным (меняем местами width и height)
                    .requiredSize(width = drawHeight, height = drawWidth)
                    .graphicsLayer {
                        // Вращаем вокруг центра (по умолчанию). Никаких костылей с translationX!
                        rotationZ = 90f
                    }
            } else {
                Modifier.requiredSize(width = drawWidth, height = drawHeight)
            }
        )
    }
}