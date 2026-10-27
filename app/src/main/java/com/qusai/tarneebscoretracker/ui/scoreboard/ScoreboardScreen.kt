package com.qusai.tarneebscoretracker.ui.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.GameStatus
import com.qusai.tarneebscoretracker.model.Round
import com.qusai.tarneebscoretracker.model.TeamSide
import com.qusai.tarneebscoretracker.ui.components.TeamScoreCard
import com.qusai.tarneebscoretracker.ui.history.HistoryRoundCard
import com.qusai.tarneebscoretracker.ui.round.RoundEditorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardScreen(
    game: GameState,
    onAddRound: (TeamSide, Int, Int, String) -> Unit,
    onEditRound: (Long, TeamSide, Int, Int, String) -> Unit,
    onDeleteRound: (Long) -> Unit,
    onUndo: () -> Unit,
    onContinue: () -> Unit,
    onNewGameSameTeams: () -> Unit,
    onNewGameNewTeams: () -> Unit,
) {
    var roundEditorOpen by remember { mutableStateOf(false) }
    var editingRound by remember { mutableStateOf<Round?>(null) }
    var roundToDelete by remember { mutableStateOf<Round?>(null) }
    var newGameDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tarneeb Score Tracker",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        StatusPill("Target ${game.targetScore}")
                        StatusPill("Round ${game.rounds.size + 1}")
                    }
                }

                item {
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        if (maxWidth < 340.dp) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                TeamScoreCard(
                                    name = game.teamOne.name,
                                    score = game.teamOneScore,
                                    isLeader = game.leader() == TeamSide.TEAM_ONE,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                TeamScoreCard(
                                    name = game.teamTwo.name,
                                    score = game.teamTwoScore,
                                    isLeader = game.leader() == TeamSide.TEAM_TWO,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                TeamScoreCard(
                                    name = game.teamOne.name,
                                    score = game.teamOneScore,
                                    isLeader = game.leader() == TeamSide.TEAM_ONE,
                                    modifier = Modifier.weight(1f),
                                )
                                TeamScoreCard(
                                    name = game.teamTwo.name,
                                    score = game.teamTwoScore,
                                    isLeader = game.leader() == TeamSide.TEAM_TWO,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                item {
                    val statusText = when {
                        game.gameStatus == GameStatus.CONTINUING -> "Playing past target"
                        game.leader() == null -> "Scores are tied"
                        else -> "${game.nameFor(game.leader()!!)} is leading"
                    }
                    Text(
                        text = statusText,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = if (game.gameStatus == GameStatus.CONTINUING) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                item {
                    Button(
                        onClick = {
                            editingRound = null
                            roundEditorOpen = true
                        },
                        enabled = game.gameStatus != GameStatus.GAME_OVER,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text("Add round")
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onUndo,
                            enabled = game.rounds.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Undo last")
                        }
                        OutlinedButton(
                            onClick = { newGameDialogOpen = true },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("New game")
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(6.dp))
                    Text("Round history", style = MaterialTheme.typography.titleLarge)
                }

                if (game.rounds.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = MaterialTheme.shapes.large,
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "No rounds yet",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = "Add the first round when bidding begins.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                } else {
                    items(game.rounds.asReversed(), key = Round::id) { round ->
                        HistoryRoundCard(
                            game = game,
                            round = round,
                            onEdit = {
                                editingRound = round
                                roundEditorOpen = true
                            },
                            onDelete = { roundToDelete = round },
                        )
                    }
                }
            }
        }
    }

    if (roundEditorOpen) {
        val originalRound = editingRound
        RoundEditorDialog(
            game = game,
            initialRound = originalRound,
            onDismiss = { roundEditorOpen = false },
            onSave = { side, bid, tricks, notes ->
                if (originalRound == null) {
                    onAddRound(side, bid, tricks, notes)
                } else {
                    onEditRound(originalRound.id, side, bid, tricks, notes)
                }
                roundEditorOpen = false
            },
        )
    }

    roundToDelete?.let { round ->
        DeleteRoundDialog(
            roundNumber = round.roundNumber,
            onDismiss = { roundToDelete = null },
            onConfirm = {
                onDeleteRound(round.id)
                roundToDelete = null
            },
        )
    }

    if (newGameDialogOpen) {
        NewGameDialog(
            onDismiss = { newGameDialogOpen = false },
            onSameTeams = {
                newGameDialogOpen = false
                onNewGameSameTeams()
            },
            onNewTeams = {
                newGameDialogOpen = false
                onNewGameNewTeams()
            },
        )
    }

    if (game.gameStatus == GameStatus.GAME_OVER) {
        GameOverDialog(
            game = game,
            onSameTeams = onNewGameSameTeams,
            onNewTeams = onNewGameNewTeams,
            onContinue = onContinue,
        )
    }
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
