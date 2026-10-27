package com.qusai.tarneebscoretracker.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.Round
import com.qusai.tarneebscoretracker.model.TeamSide
import com.qusai.tarneebscoretracker.ui.round.isSpecial
import com.qusai.tarneebscoretracker.ui.round.label

@Composable
fun HistoryRoundCard(
    game: GameState,
    round: Round,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val biddingName = game.nameFor(round.biddingTeam)
    val opposingSide = if (round.biddingTeam == TeamSide.TEAM_ONE) {
        TeamSide.TEAM_TWO
    } else {
        TeamSide.TEAM_ONE
    }
    val statusColor = if (round.biddingTeamSucceeded) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val containerColor = if (round.biddingTeamSucceeded) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.28f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.28f)),
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Round ${round.roundNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (round.biddingTeamSucceeded) "SUCCESS" else "FAILED",
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$biddingName bid ${round.bid} and won ${round.biddingTeamTricks} tricks — " +
                    if (round.biddingTeamSucceeded) "Success" else "Failure",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "${game.nameFor(opposingSide)} won ${round.opposingTeamTricks} tricks",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${game.teamOne.name} ${round.teamOneScoreChange.signed()}  •  " +
                    "${game.teamTwo.name} ${round.teamTwoScoreChange.signed()}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Score: ${game.teamOne.name} ${round.teamOneTotalAfter} — " +
                    "${game.teamTwo.name} ${round.teamTwoTotalAfter}",
                style = MaterialTheme.typography.bodyMedium,
            )

            if (round.scoringType.isSpecial()) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(round.scoringType.label()) },
                )
            }
            if (round.notes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Note: ${round.notes}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun Int.signed(): String = if (this >= 0) "+$this" else toString()
