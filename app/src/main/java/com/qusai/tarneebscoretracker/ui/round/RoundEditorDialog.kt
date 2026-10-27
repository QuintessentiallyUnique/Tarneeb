package com.qusai.tarneebscoretracker.ui.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.Round
import com.qusai.tarneebscoretracker.model.ScoringType
import com.qusai.tarneebscoretracker.model.TeamSide
import com.qusai.tarneebscoretracker.scoring.TarneebScoring

@Composable
fun RoundEditorDialog(
    game: GameState,
    initialRound: Round? = null,
    onDismiss: () -> Unit,
    onSave: (TeamSide, Int, Int, String) -> Unit,
) {
    var biddingTeam by remember(initialRound?.id) {
        mutableStateOf(initialRound?.biddingTeam ?: TeamSide.TEAM_ONE)
    }
    var bidText by remember(initialRound?.id) {
        mutableStateOf(initialRound?.bid?.toString() ?: "7")
    }
    var tricksText by remember(initialRound?.id) {
        mutableStateOf(initialRound?.biddingTeamTricks?.toString() ?: "7")
    }
    var notes by remember(initialRound?.id) { mutableStateOf(initialRound?.notes.orEmpty()) }
    var bidError by remember(initialRound?.id) { mutableStateOf<String?>(null) }
    var tricksError by remember(initialRound?.id) { mutableStateOf<String?>(null) }

    val validBid = bidText.toIntOrNull()?.takeIf { it in 7..13 }
    val validTricks = tricksText.toIntOrNull()?.takeIf { it in 0..13 }
    val preview = if (validBid != null && validTricks != null) {
        TarneebScoring.calculateRoundScore(biddingTeam, validBid, validTricks)
    } else null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 560.dp)
                .heightIn(max = 720.dp)
                .imePadding(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = if (initialRound == null) "Add round" else "Edit round ${initialRound.roundNumber}",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(Modifier.height(20.dp))

                Text("Bidding team", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TeamSide.entries.forEach { side ->
                        FilterChip(
                            selected = biddingTeam == side,
                            onClick = { biddingTeam = side },
                            label = { Text(game.nameFor(side)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))

                NumberEntry(
                    label = "Bid",
                    value = bidText,
                    range = 7..13,
                    error = bidError,
                    onValueChange = {
                        bidText = it
                        bidError = null
                    },
                )
                Spacer(Modifier.height(16.dp))
                NumberEntry(
                    label = "Tricks won by ${game.nameFor(biddingTeam)}",
                    value = tricksText,
                    range = 0..13,
                    error = tricksError,
                    onValueChange = {
                        tricksText = it
                        tricksError = null
                    },
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes (optional)") },
                    minLines = 2,
                    maxLines = 4,
                )

                if (preview != null) {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = if (preview.biddingTeamSucceeded) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        },
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = if (preview.biddingTeamSucceeded) "Bid succeeds" else "Bid fails",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = buildString {
                                    append(game.nameFor(biddingTeam))
                                    append(" ")
                                    append(preview.biddingTeamScoreChange.withSign())
                                    append("  •  ")
                                    append(game.nameFor(if (biddingTeam == TeamSide.TEAM_ONE) TeamSide.TEAM_TWO else TeamSide.TEAM_ONE))
                                    append(" ")
                                    append(preview.opposingTeamScoreChange.withSign())
                                },
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (preview.scoringType.isSpecial()) {
                                Text(
                                    text = preview.scoringType.label(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val bid = bidText.toIntOrNull()
                            val tricks = tricksText.toIntOrNull()
                            bidError = when {
                                bid == null -> "Enter a bid."
                                bid !in 7..13 -> "Bid must be from 7 to 13."
                                else -> null
                            }
                            tricksError = when {
                                tricks == null -> "Enter tricks won."
                                tricks !in 0..13 -> "Tricks must be from 0 to 13."
                                else -> null
                            }
                            if (bidError == null && tricksError == null && bid != null && tricks != null) {
                                onSave(biddingTeam, bid, tricks, notes)
                            }
                        },
                    ) {
                        Text(if (initialRound == null) "Add round" else "Save changes")
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberEntry(
    label: String,
    value: String,
    range: IntRange,
    error: String?,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = {
                    val current = value.toIntOrNull() ?: range.first
                    onValueChange((current - 1).coerceIn(range).toString())
                },
                modifier = Modifier.height(56.dp),
            ) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit).take(2)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                isError = error != null,
                supportingText = error?.let { message -> { Text(message) } },
            )
            OutlinedButton(
                onClick = {
                    val current = value.toIntOrNull() ?: range.first
                    onValueChange((current + 1).coerceIn(range).toString())
                },
                modifier = Modifier.height(56.dp),
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

private fun Int.withSign(): String = if (this >= 0) "+$this" else toString()

fun ScoringType.isSpecial(): Boolean = when (this) {
    ScoringType.ALL_TRICKS_WITHOUT_13_BID,
    ScoringType.BID_13_SUCCESS,
    ScoringType.BID_13_FAILURE,
    -> true
    else -> false
}

fun ScoringType.label(): String = when (this) {
    ScoringType.NORMAL_SUCCESS -> "Normal success"
    ScoringType.NORMAL_FAILURE -> "Normal failure"
    ScoringType.ALL_TRICKS_WITHOUT_13_BID -> "All 13 tricks"
    ScoringType.BID_13_SUCCESS -> "13 bid won"
    ScoringType.BID_13_FAILURE -> "Failed 13 bid"
}
