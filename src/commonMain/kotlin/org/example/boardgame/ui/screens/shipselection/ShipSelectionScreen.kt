package org.example.boardgame.ui.screens.shipselection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun ShipSelectionScreen(
    viewModel: ShipSelectionViewModel,
    onSelectionFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onSelectionFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${uiState.currentPlayerName}, choose your special ship!",
            style = MaterialTheme.typography.headlineMedium,
            color = PencilDark,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "This unique ship will be available during placement.",
            style = MaterialTheme.typography.bodyMedium,
            color = PencilDark.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.availableShapes) { shape ->
                ShapeOption(
                    shape = shape,
                    isSelected = uiState.selectedShape == shape,
                    onClick = { viewModel.onShapeSelected(shape) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PencilButton(
            text = "Confirm Selection",
            onClick = { viewModel.onConfirmSelection() },
            enabled = uiState.selectedShape != null,
            modifier = Modifier.fillMaxWidth(0.6f),
            backgroundColor = if (uiState.selectedShape != null) PencilGreen else Color.LightGray,
            contentColor = Color.White
        )
    }
}

@Composable
private fun ShapeOption(
    shape: SpecialShape,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PencilGreen else PencilLight.copy(alpha = 0.3f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .border(if (isSelected) 3.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PencilGreen.copy(alpha = 0.05f) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp),
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
                    drawWidth = 100.dp,
                    drawHeight = 100.dp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = shape.name.replace("_", " "),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PencilGreen else PencilDark
            )
        }
    }
}
