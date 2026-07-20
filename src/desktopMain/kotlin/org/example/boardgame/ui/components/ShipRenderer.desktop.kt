package org.example.boardgame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import battleship.Ship
import common.ShipType
import common.SpecialShape

@Composable
actual fun ShipRenderer(
    ship: Ship,
    offsetX: Dp,
    offsetY: Dp,
    drawWidth: Dp,
    drawHeight: Dp
) {
    val resName = when (ship.type) {
        ShipType.SUBMARINE -> "drawable/ship_1.png"
        ShipType.DESTROYER -> "drawable/ship_22.png"
        ShipType.CRUISER -> "drawable/ship_3.png"
        ShipType.CARRIER -> "drawable/ship_4.png"
        ShipType.BATTLE_WAGON -> "drawable/ship_5.png"
        ShipType.SPECIAL -> when (ship.shape) {
            SpecialShape.TOP_LEFT -> "drawable/j_right_ship_5.png"
            SpecialShape.TOP_RIGHT -> "drawable/j_left_ship_5.png"
            SpecialShape.MIDDLE_LEFT -> "drawable/t_lev_ship_5.png"
            SpecialShape.MIDDLE_RIGHT -> "drawable/t_right_ship_5.png"
            SpecialShape.BOTTOM_LEFT -> "drawable/bot_lev_ship_5_1.png"
            SpecialShape.BOTTOM_RIGHT -> "drawable/bot_right_ship_5_1.png"
            null -> "drawable/ship_4.png"
        }
    }

    val isVertical = drawHeight > drawWidth && ship.type != ShipType.SPECIAL

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(drawWidth, drawHeight)
    ) {
        Image(
            painter = painterResource(resName),
            contentDescription = null,
            modifier = if (isVertical) {
                Modifier
                    .requiredSize(drawHeight, drawWidth)
                    .graphicsLayer {
                        rotationZ = 90f
                        transformOrigin = TransformOrigin(0f, 0f)
                        translationX = size.height
                    }
            } else {
                Modifier.size(drawWidth, drawHeight)
            },
            contentScale = ContentScale.FillBounds
        )
    }
}
