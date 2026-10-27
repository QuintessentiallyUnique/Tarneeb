package com.qusai.tarneebscoretracker.scoring

import com.qusai.tarneebscoretracker.model.ScoringType
import com.qusai.tarneebscoretracker.model.TeamSide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TarneebScoringTest {
    @Test
    fun `bid 9 and 10 tricks is normal success`() {
        val result = score(bid = 9, tricks = 10)

        assertEquals(10, result.biddingTeamScoreChange)
        assertEquals(0, result.opposingTeamScoreChange)
        assertTrue(result.biddingTeamSucceeded)
        assertEquals(ScoringType.NORMAL_SUCCESS, result.scoringType)
    }

    @Test
    fun `bid 10 and 8 tricks is normal failure`() {
        val result = score(bid = 10, tricks = 8)

        assertEquals(-10, result.biddingTeamScoreChange)
        assertEquals(5, result.opposingTeamScoreChange)
        assertFalse(result.biddingTeamSucceeded)
        assertEquals(ScoringType.NORMAL_FAILURE, result.scoringType)
    }

    @Test
    fun `winning all tricks without bidding 13 scores 16`() {
        val result = score(bid = 10, tricks = 13)

        assertEquals(16, result.biddingTeamScoreChange)
        assertEquals(0, result.opposingTeamScoreChange)
        assertTrue(result.biddingTeamSucceeded)
        assertEquals(ScoringType.ALL_TRICKS_WITHOUT_13_BID, result.scoringType)
    }

    @Test
    fun `bid 13 and win 13 scores 26`() {
        val result = score(bid = 13, tricks = 13)

        assertEquals(26, result.biddingTeamScoreChange)
        assertEquals(0, result.opposingTeamScoreChange)
        assertTrue(result.biddingTeamSucceeded)
        assertEquals(ScoringType.BID_13_SUCCESS, result.scoringType)
    }

    @Test
    fun `failed 13 bid loses 16 while opponents score tricks`() {
        val result = score(bid = 13, tricks = 11)

        assertEquals(-16, result.biddingTeamScoreChange)
        assertEquals(2, result.opposingTeamScoreChange)
        assertFalse(result.biddingTeamSucceeded)
        assertEquals(ScoringType.BID_13_FAILURE, result.scoringType)
    }

    @Test
    fun `minimum bid succeeds at exactly 7 tricks`() {
        val result = score(bid = 7, tricks = 7)

        assertEquals(7, result.biddingTeamScoreChange)
        assertEquals(0, result.opposingTeamScoreChange)
        assertTrue(result.biddingTeamSucceeded)
    }

    @Test
    fun `minimum bid failure awards 7 opposing tricks`() {
        val result = score(bid = 7, tricks = 6)

        assertEquals(-7, result.biddingTeamScoreChange)
        assertEquals(7, result.opposingTeamScoreChange)
        assertFalse(result.biddingTeamSucceeded)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `bid outside range is rejected`() {
        score(bid = 6, tricks = 7)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `tricks outside range is rejected`() {
        score(bid = 7, tricks = 14)
    }

    private fun score(bid: Int, tricks: Int) = TarneebScoring.calculateRoundScore(
        biddingTeam = TeamSide.TEAM_ONE,
        bid = bid,
        biddingTeamTricks = tricks,
    )
}
