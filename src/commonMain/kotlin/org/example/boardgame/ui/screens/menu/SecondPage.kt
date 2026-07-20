package org.example.boardgame.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.boardgame.ui.components.MenuButton
import org.example.boardgame.ui.theme.SeaBattleTheme

@Composable
fun SecondPage(
    onStartNewGame: () -> Unit,
    onViewPlayerStats: () -> Unit,
    onGetLeaderboard: () -> Unit,
    onViewGameHistory: () -> Unit,
    onExit: () -> Unit
) {
    SeaBattleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "Start a new game",
                    onClick = onStartNewGame
                )
                MenuButton(
                    text = "View player statistics",
                    onClick = onViewPlayerStats
                )
                MenuButton(
                    text = "Get leaderboard statistics",
                    onClick = onGetLeaderboard
                )
                MenuButton(
                    text = "View game history and get a replay of a ID match",
                    onClick = onViewGameHistory
                )
                MenuButton(
                    text = "Exit",
                    onClick = onExit
                )
            }
        }
    }
}
