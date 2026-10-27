package com.qusai.tarneebscoretracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qusai.tarneebscoretracker.data.GameRepository
import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.GameStatus
import com.qusai.tarneebscoretracker.model.RoundInput
import com.qusai.tarneebscoretracker.model.TeamSide
import com.qusai.tarneebscoretracker.scoring.GameCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class GameUiState(
    val isLoading: Boolean = true,
    val game: GameState = GameState(),
)

class GameViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private val saveMutex = Mutex()

    init {
        viewModelScope.launch {
            repository.gameState.collect { saved ->
                _uiState.value = GameUiState(
                    isLoading = false,
                    game = saved ?: GameState(),
                )
            }
        }
    }

    fun setupGame(teamOneName: String, teamTwoName: String, targetScore: Int) {
        commit(GameCalculator.newGame(teamOneName, teamTwoName, targetScore))
    }

    fun addRound(biddingTeam: TeamSide, bid: Int, tricks: Int, notes: String) {
        val current = _uiState.value.game
        if (current.gameStatus == GameStatus.GAME_OVER) return
        val nextId = maxOf(
            System.currentTimeMillis(),
            (current.rounds.maxOfOrNull { it.id } ?: 0L) + 1L,
        )
        commit(
            GameCalculator.appendRound(
                current,
                RoundInput(nextId, biddingTeam, bid, tricks, notes),
            ),
        )
    }

    fun editRound(roundId: Long, biddingTeam: TeamSide, bid: Int, tricks: Int, notes: String) {
        val current = _uiState.value.game
        if (current.rounds.none { it.id == roundId }) return
        commit(
            GameCalculator.replaceRound(
                current,
                RoundInput(roundId, biddingTeam, bid, tricks, notes),
            ),
        )
    }

    fun deleteRound(roundId: Long) {
        commit(GameCalculator.deleteRound(_uiState.value.game, roundId))
    }

    fun undoLastRound() {
        val current = _uiState.value.game
        if (current.rounds.isNotEmpty()) commit(GameCalculator.undoLastRound(current))
    }

    fun continueAfterGameOver() {
        val current = _uiState.value.game
        if (current.gameStatus == GameStatus.GAME_OVER) {
            commit(
                current.copy(
                    gameStatus = GameStatus.CONTINUING,
                    continuedAfterGameOver = true,
                ),
            )
        }
    }

    fun startNewGameWithSameTeams() {
        val current = _uiState.value.game
        commit(
            GameCalculator.newGame(
                current.teamOne.name,
                current.teamTwo.name,
                current.targetScore,
            ),
        )
    }

    fun startNewGameWithNewTeams() {
        commit(GameState())
    }

    private fun commit(gameState: GameState) {
        _uiState.value = GameUiState(isLoading = false, game = gameState)
        viewModelScope.launch {
            saveMutex.withLock { repository.saveGame(gameState) }
        }
    }

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(GameViewModel::class.java))
            return GameViewModel(repository) as T
        }
    }
}
