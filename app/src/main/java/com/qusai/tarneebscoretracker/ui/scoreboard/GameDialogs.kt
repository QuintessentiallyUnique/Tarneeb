package com.qusai.tarneebscoretracker.ui.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.qusai.tarneebscoretracker.model.GameState

@Composable
fun GameOverDialog(
    game: GameState,
    onSameTeams: () -> Unit,
    onNewTeams: () -> Unit,
    onContinue: () -> Unit,
) {
    val winner = game.winnerAtTarget()
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 8.dp,
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = if (winner == null) "Tie at target score" else "Game over",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                if (winner != null) {
                    Text(
                        text = "${game.nameFor(winner)} wins!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = "Final score\n${game.teamOne.name} ${game.teamOneScore} — " +
                        "${game.teamTwo.name} ${game.teamTwoScore}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onSameTeams,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("New game with same teams") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNewTeams,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("New game with new teams") }
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Continue anyway") }
            }
        }
    }
}

@Composable
fun NewGameDialog(
    onDismiss: () -> Unit,
    onSameTeams: () -> Unit,
    onNewTeams: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start new game?") },
        text = { Text("The current score and round history will be replaced.") },
        confirmButton = {
            Button(onClick = onSameTeams) { Text("Same teams") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onNewTeams) { Text("New teams") }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
    )
}

@Composable
fun DeleteRoundDialog(
    roundNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete round $roundNumber?") },
        text = { Text("All following totals will be recalculated automatically.") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
