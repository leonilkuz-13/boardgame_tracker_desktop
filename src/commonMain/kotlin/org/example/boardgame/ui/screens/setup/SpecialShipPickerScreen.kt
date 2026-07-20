package org.example.boardgame.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import battleship.Ship
import common.Coordinate
import common.ShipType
import common.SpecialShape
import org.example.boardgame.ui.components.PencilButton
import org.example.boardgame.ui.components.ShipRenderer
import org.example.boardgame.ui.theme.*

@Composable
fun SpecialShipPickerScreen(
    viewModel: SpecialShipPickerViewModel,
    onConfirmed: (SpecialShape) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clickable(enabled = false) {}, // Prevent closing when clicking card
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = androidx.compose.foundation.BorderStroke(2.dp, PencilDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Special Ship Shape",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PencilDark,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.availableShapes) { shape ->
                        ShapeItem(
                            shape = shape,
                            isSelected = uiState.selectedShape == shape,
                            onClick = { viewModel.onShapeSelected(shape) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PencilButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PencilButton(
                        text = "Confirm",
                        onClick = { uiState.selectedShape?.let { onConfirmed(it) } },
                        enabled = uiState.selectedShape != null,
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (uiState.selectedShape != null) PencilGreen else Color.LightGray,
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapeItem(
    shape: SpecialShape,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PencilGreen else PencilLight.copy(alpha = 0.3f)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(if (isSelected) PencilGreen.copy(alpha = 0.05f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Fake ship object for rendering
            val dummyShip = object : Ship {
                override val coordinates = emptyList<Coordinate>()
                override val type = ShipType.SPECIAL
                override val shape = shape
                override fun receiveHit(coordinate: Coordinate) {}
                override fun isSunk() = false
            }
            
            ShipRenderer(
                ship = dummyShip,
                offsetX = 0.dp,
                offsetY = 0.dp,
                drawWidth = 80.dp,
                drawHeight = 80.dp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = shape.name.replace("_", " "),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) PencilGreen else PencilDark
        )
    }
}
