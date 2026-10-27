package com.qusai.tarneebscoretracker.scoring

import com.qusai.tarneebscoretracker.model.GameState
import com.qusai.tarneebscoretracker.model.GameStatus
import com.qusai.tarneebscoretracker.model.RoundInput
import com.qusai.tarneebscoretracker.model.TeamSide
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCalculatorTest {
    @Test
    fun `editing an early round recalculates every following total`() {
        val initial = gameWithTwoRounds()
        val edited = GameCalculator.replaceRound(
            initial,
            RoundInput(
                id = 1L,
                biddingTeam = TeamSide.TEAM_ONE,
                bid = 10,
                biddingTeamTricks = 8,
            ),
        )

        assertEquals(-5, edited.teamOneScore)
        assertEquals(-5, edited.teamTwoScore)
        assertEquals(-5, edited.rounds.last().teamOneTotalAfter)
        assertEquals(-5, edited.rounds.last().teamTwoTotalAfter)
    }

    @Test
    fun `deleting a round replays and renumbers remaining history`() {
        val deleted = GameCalculator.deleteRound(gameWithTwoRounds(), roundId = 1L)

        assertEquals(1, deleted.rounds.size)
        assertEquals(1, deleted.rounds.single().roundNumber)
        assertEquals(5, deleted.teamOneScore)
        assertEquals(-10, deleted.teamTwoScore)
    }

    @Test
    fun `undo removes the latest round and restores prior totals`() {
        val undone = GameCalculator.undoLastRound(gameWithTwoRounds())

        assertEquals(1, undone.rounds.size)
        assertEquals(10, undone.teamOneScore)
        assertEquals(0, undone.teamTwoScore)
    }

    @Test
    fun `target creates game over and continue flag is retained`() {
        val base = GameCalculator.newGame("Falcons", "Wolves", 7)
        val finished = GameCalculator.appendRound(
            base,
            RoundInput(1L, TeamSide.TEAM_ONE, bid = 7, biddingTeamTricks = 7),
        )
        assertEquals(GameStatus.GAME_OVER, finished.gameStatus)
        assertEquals(TeamSide.TEAM_ONE, finished.winnerAtTarget())

        val continuing = finished.copy(
            gameStatus = GameStatus.CONTINUING,
            continuedAfterGameOver = true,
        )
        val afterAnotherRound = GameCalculator.appendRound(
            continuing,
            RoundInput(2L, TeamSide.TEAM_TWO, bid = 7, biddingTeamTricks = 7),
        )

        assertEquals(GameStatus.CONTINUING, afterAnotherRound.gameStatus)
        assertTrue(afterAnotherRound.continuedAfterGameOver)
    }

    @Test
    fun `game state serializes with complete history`() {
        val original = gameWithTwoRounds()
        val json = Json { encodeDefaults = true }

        val encoded = json.encodeToString(GameState.serializer(), original)
        val restored = json.decodeFromString(GameState.serializer(), encoded)

        assertEquals(original, restored)
        assertFalse(restored.rounds.isEmpty())
    }

    private fun gameWithTwoRounds(): GameState {
        val base = GameCalculator.newGame("Falcons", "Wolves", 31)
        return GameCalculator.recalculate(
            base,
            listOf(
                RoundInput(1L, TeamSide.TEAM_ONE, bid = 9, biddingTeamTricks = 10),
                RoundInput(2L, TeamSide.TEAM_TWO, bid = 10, biddingTeamTricks = 8),
            ),
        )
    }
}
