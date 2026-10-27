package com.qusai.tarneebscoretracker.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing

private enum class TargetChoice(val value: Int?) {
    THIRTY_ONE(31),
    FORTY_ONE(41),
    SIXTY_ONE(61),
    CUSTOM(null),
}

@Composable
fun SetupScreen(
    onStartGame: (String, String, Int) -> Unit,
) {
    var teamOne by remember { mutableStateOf("Team 1") }
    var teamTwo by remember { mutableStateOf("Team 2") }
    var targetChoice by remember { mutableStateOf(TargetChoice.THIRTY_ONE) }
    var customTarget by remember { mutableStateOf("") }
    var teamError by remember { mutableStateOf<String?>(null) }
    var targetError by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Tarneeb Score Tracker",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Set up your teams and start keeping score.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = teamOne,
                    onValueChange = {
                        teamOne = it
                        teamError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Team 1 name") },
                    singleLine = true,
                    isError = teamError != null,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = teamTwo,
                    onValueChange = {
                        teamTwo = it
                        teamError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Team 2 name") },
                    singleLine = true,
                    isError = teamError != null,
                    supportingText = teamError?.let { error -> { Text(error) } },
                )
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Target score",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TargetChoice.entries.forEach { choice ->
                        FilterChip(
                            selected = targetChoice == choice,
                            onClick = {
                                targetChoice = choice
                                targetError = null
                            },
                            label = { Text(choice.value?.toString() ?: "Custom") },
                        )
                    }
                }
                if (targetChoice == TargetChoice.CUSTOM) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customTarget,
                        onValueChange = {
                            customTarget = it.filter(Char::isDigit)
                            targetError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Custom target") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = targetError != null,
                        supportingText = targetError?.let { error -> { Text(error) } },
                    )
                } else if (targetError != null) {
                    Text(
                        text = targetError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        val target = targetChoice.value ?: customTarget.toIntOrNull()
                        teamError = if (teamOne.isBlank() && teamTwo.isBlank()) {
                            "Team names cannot both be empty."
                        } else null
                        targetError = if (target == null || target <= 0) {
                            "Enter a target score greater than 0."
                        } else null
                        if (teamError == null && targetError == null && target != null) {
                            onStartGame(teamOne, teamTwo, target)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text("Start game")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
