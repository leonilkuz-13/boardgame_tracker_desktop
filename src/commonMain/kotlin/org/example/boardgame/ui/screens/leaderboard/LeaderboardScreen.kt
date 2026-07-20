package org.example.boardgame.ui.screens.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import common.PlayerStats
import org.example.boardgame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadLeaderboard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperWhite,
                    titleContentColor = PencilDark,
                    navigationIconContentColor = PencilDark,
                    actionIconContentColor = PencilDark
                )
            )
        },
        containerColor = PaperWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is LeaderboardUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PencilDark)
                    }
                }
                is LeaderboardUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = PencilRed, textAlign = TextAlign.Center)
                    }
                }
                is LeaderboardUiState.Success -> {
                    LeaderboardContent(
                        players = state.players,
                        currentSort = state.currentSort,
                        onSortSelected = { viewModel.onSortOptionSelected(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardContent(
    players: List<PlayerStats>,
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortTab(
                label = "Wins",
                isSelected = currentSort == SortOption.WINS,
                onClick = { onSortSelected(SortOption.WINS) },
                modifier = Modifier.weight(1f)
            )
            SortTab(
                label = "Win Rate",
                isSelected = currentSort == SortOption.WIN_RATE,
                onClick = { onSortSelected(SortOption.WIN_RATE) },
                modifier = Modifier.weight(1f)
            )
            SortTab(
                label = "Total",
                isSelected = currentSort == SortOption.TOTAL_GAMES,
                onClick = { onSortSelected(SortOption.TOTAL_GAMES) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", Modifier.width(30.dp), fontWeight = FontWeight.Bold, color = PencilLight, fontSize = 12.sp)
            Text("Player", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = PencilLight, fontSize = 12.sp)
            Text("W", Modifier.width(40.dp), textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = PencilLight, fontSize = 12.sp)
            Text("%", Modifier.width(50.dp), textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = PencilLight, fontSize = 12.sp)
        }

        HorizontalDivider(color = GridLineColor, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(players) { index, player ->
                PlayerRow(rank = index + 1, player = player)
            }
        }
    }
}

@Composable
private fun SortTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) PencilDark else Color.Transparent
    val contentColor = if (isSelected) PaperWhite else PencilDark

    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, PencilDark) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

@Composable
private fun PlayerRow(rank: Int, player: PlayerStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GridLineColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.width(30.dp),
                fontSize = 14.sp,
                color = if (rank <= 3) PencilRed else PencilLight,
                fontWeight = if (rank <= 3) FontWeight.ExtraBold else FontWeight.Normal
            )
            Text(
                text = player.playerName,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PencilDark
            )
            Text(
                text = player.gamesWon.toString(),
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                color = PencilGreen
            )
            Text(
                text = "${player.winRate}%",
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.End,
                fontSize = 14.sp,
                color = PenBlue
            )
        }
    }
}
