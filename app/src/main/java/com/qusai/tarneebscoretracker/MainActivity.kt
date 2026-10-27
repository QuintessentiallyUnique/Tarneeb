package com.qusai.tarneebscoretracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qusai.tarneebscoretracker.data.DefaultGameRepository
import com.qusai.tarneebscoretracker.data.LocalGameStorage
import com.qusai.tarneebscoretracker.ui.scoreboard.ScoreboardScreen
import com.qusai.tarneebscoretracker.ui.setup.SetupScreen
import com.qusai.tarneebscoretracker.ui.theme.TarneebScoreTrackerTheme
import com.qusai.tarneebscoretracker.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        DefaultGameRepository(LocalGameStorage(applicationContext))
    }
    private val gameViewModel by viewModels<GameViewModel> {
        GameViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TarneebScoreTrackerTheme {
                TarneebApp(gameViewModel)
            }
        }
    }
}

@Composable
private fun TarneebApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        !uiState.game.hasCompletedSetup -> SetupScreen(
            onStartGame = viewModel::setupGame,
        )

        else -> ScoreboardScreen(
            game = uiState.game,
            onAddRound = viewModel::addRound,
            onEditRound = viewModel::editRound,
            onDeleteRound = viewModel::deleteRound,
            onUndo = viewModel::undoLastRound,
            onContinue = viewModel::continueAfterGameOver,
            onNewGameSameTeams = viewModel::startNewGameWithSameTeams,
            onNewGameNewTeams = viewModel::startNewGameWithNewTeams,
        )
    }
}
