package org.example.boardgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.room.Room
import gamemanager.GameManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.example.boardgame.db.AppDatabase
import org.example.boardgame.db.impl.AndroidHistoryImpl
import org.example.boardgame.db.impl.AndroidStatisticsImpl
import org.example.boardgame.ui.screens.GameResult.GameResultScreen
import org.example.boardgame.ui.screens.GameResult.GameResultViewModel
import org.example.boardgame.ui.screens.auth.LoginScreen
import org.example.boardgame.ui.screens.auth.LoginViewModel
import org.example.boardgame.ui.screens.combat.CombatScreen
import org.example.boardgame.ui.screens.combat.CombatViewModel
import org.example.boardgame.ui.screens.history.HistorySearchScreen
import org.example.boardgame.ui.screens.history.HistorySearchViewModel
import org.example.boardgame.ui.screens.history.MatchReplayScreen
import org.example.boardgame.ui.screens.history.MatchReplayViewModel
import org.example.boardgame.ui.screens.leaderboard.LeaderboardScreen
import org.example.boardgame.ui.screens.leaderboard.LeaderboardViewModel
import org.example.boardgame.ui.screens.menu.FirstPage
import org.example.boardgame.ui.screens.menu.SecondPage
import org.example.boardgame.ui.screens.shipselection.ShipSelectionScreen
import org.example.boardgame.ui.screens.shipselection.ShipSelectionViewModel
import org.example.boardgame.ui.screens.stats.PlayerStatsScreen
import org.example.boardgame.ui.screens.stats.PlayerStatsViewModel
import org.example.boardgame.ui.setup.SetupScreen
import org.example.boardgame.ui.setup.SetupViewModel
import org.example.boardgame.ui.theme.SeaBattleTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "battleship-db"
        )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()
    }

    private val gameManager by lazy {
        GameManagerImpl(
            stat = AndroidStatisticsImpl(db.playerDao()),
            history = AndroidHistoryImpl(db.historyDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeaBattleTheme {
                var screenState by rememberSaveable { mutableStateOf(0) }
                var winnerName by rememberSaveable { mutableStateOf("") }
                var selectedMatchId by rememberSaveable { mutableIntStateOf(0) }

                when (screenState) {
                    0 -> FirstPage(onContinue = { screenState = 1 })
                    1 -> SecondPage(
                        onStartNewGame = {
                            screenState = 8
                        },
                        onViewPlayerStats = { screenState = 5 },
                        onGetLeaderboard = { screenState = 6 },
                        onViewGameHistory = { screenState = 9 },
                        onExit = { finish() }
                    )
                    2 -> {
                        val viewModel = remember { SetupViewModel(gameManager) }
                        SetupScreen(viewModel = viewModel, onNavigateToCombat = { screenState = 3 })
                    }
                    3 -> {
                        val viewModel = remember { CombatViewModel(gameManager) }
                        CombatScreen(viewModel = viewModel, onBackToMenu = { screenState = 1 })
                    }
                    5 -> {
                        val scope = rememberCoroutineScope()
                        val viewModel = remember { PlayerStatsViewModel(gameManager, scope) }
                        PlayerStatsScreen(viewModel = viewModel, onBack = { screenState = 1 })
                    }
                    6 -> {
                        val scope = rememberCoroutineScope()
                        val viewModel = remember { LeaderboardViewModel(gameManager, scope) }
                        LeaderboardScreen(viewModel = viewModel, onBack = { screenState = 1 })
                    }
                    7 -> {
                        val viewModel = remember { ShipSelectionViewModel(gameManager) }
                        ShipSelectionScreen(viewModel = viewModel, onSelectionFinished = { screenState = 2 })
                    }
                    8 -> {
                        val viewModel = remember { LoginViewModel(gameManager) }
                        LoginScreen(
                            viewModel = viewModel, 
                            onSuccess = { screenState = 7 },
                            onBack = { screenState = 1 }
                        )
                    }
                    9 -> {
                        val viewModel = remember { HistorySearchViewModel(gameManager) }
                        HistorySearchScreen(
                            viewModel = viewModel,
                            onMatchFound = { id -> 
                                selectedMatchId = id
                                screenState = 10 
                            },
                            onBack = { screenState = 1 }
                        )
                    }
                    10 -> {
                        val id = selectedMatchId
                        val viewModel = remember(id) { MatchReplayViewModel(id, gameManager) }
                        MatchReplayScreen(viewModel = viewModel, onBack = { screenState = 9 })
                    }
                }
            }
        }
    }
}
