package org.example.boardgame.ui.screens.stats

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.boardgame.ui.components.PencilButton
import org.example.boardgame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(
    viewModel: PlayerStatsViewModel,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Statistics", color = PencilDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PencilDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite)
            )
        },
        containerColor = PaperWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.widthIn(max = 400.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter player name...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PencilLight) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onClearSearch() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = PencilLight)
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.onSearchClicked()
                            focusManager.clearFocus()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PencilDark,
                            unfocusedBorderColor = PencilLight,
                            cursorColor = PencilDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PencilButton(
                        text = "Search",
                        onClick = {
                            viewModel.onSearchClicked()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = uiState) {
                    is PlayerStatsUiState.Idle -> {
                        Text(
                            "Enter a nickname to view the warrior's history.",
                            color = PencilLight,
                            fontSize = 14.sp
                        )
                    }
                    is PlayerStatsUiState.Loading -> {
                        CircularProgressIndicator(color = PencilDark)
                    }
                    is PlayerStatsUiState.Error -> {
                        Text(
                            state.message,
                            color = PencilRed,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                    is PlayerStatsUiState.Success -> {
                        StatsCard(state.stats)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(stats: common.PlayerStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, PencilDark, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stats.playerName,
                style = MaterialTheme.typography.headlineMedium,
                color = PencilDark,
                fontWeight = FontWeight.ExtraBold
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = GridLineColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Played", value = stats.gamesPlayed.toString())
                StatItem(label = "Won", value = stats.gamesWon.toString(), color = PencilGreen)
                StatItem(label = "Win Rate", value = "${stats.winRate}%", color = PenBlue)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color = PencilDark) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = PencilLight)
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
