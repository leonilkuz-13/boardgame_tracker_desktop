package org.example.boardgame.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import battleship.Ship

@Composable
expect fun ShipRenderer(
    ship: Ship,
    offsetX: Dp,
    offsetY: Dp,
    drawWidth: Dp,
    drawHeight: Dp
)
