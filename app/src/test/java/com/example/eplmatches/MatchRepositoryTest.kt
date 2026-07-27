package com.example.eplmatches

import com.example.eplmatches.data.api.MatchApiService
import com.example.eplmatches.data.api.MatchDto
import com.example.eplmatches.data.local.MatchDao
import com.example.eplmatches.data.local.MatchEntity
import com.example.eplmatches.data.repository.MatchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class MatchRepositoryTest {

    private lateinit var apiService: MatchApiService
    private lateinit var matchDao: MatchDao
    private lateinit var repository: MatchRepository

    @Before
    fun setup() {
        apiService = mockk()
        matchDao = mockk()
        repository = MatchRepository(apiService, matchDao)
    }

    @Test
    fun `getAllMatches returns cached data from DAO`() = runTest {
        val entities = listOf(
            MatchEntity(
                matchNumber = 1,
                dateUtc = "2023-08-11T19:00:00Z",
                status = "FullTime",
                homeTeam = "Team A",
                awayTeam = "Team B",
                homeTeamScore = 2,
                awayTeamScore = 1,
                roundNumber = 1,
                location = "Stadium",
                group = null
            )
        )

        every { matchDao.getAllMatches() } returns flowOf(entities)

        val matches = repository.getAllMatches().first()

        assertEquals(1, matches.size)
        assertEquals("Team A", matches[0].homeTeam)
        assertEquals("Team B", matches[0].awayTeam)
        assertEquals(2, matches[0].homeTeamScore)
        assertEquals(1, matches[0].awayTeamScore)
        assertNotNull(matches[0].localDate)
        assertNotNull(matches[0].localTime)
    }

    @Test
    fun `refreshMatches fetches from API and saves to DAO`() = runTest {
        val dtos = listOf(
            MatchDto(
                dateUtc = "2023-08-11T19:00:00Z",
                status = "FullTime",
                homeTeam = "Team A",
                awayTeam = "Team B",
                homeTeamScore = 2,
                awayTeamScore = 1,
                matchNumber = 1,
                roundNumber = 1,
                location = "Stadium",
                group = null
            )
        )

        coEvery { apiService.getMatches() } returns dtos
        coEvery { matchDao.deleteAll() } returns Unit
        coEvery { matchDao.insertAll(any()) } returns Unit

        repository.refreshMatches()

        coVerify { matchDao.deleteAll() }
        coVerify { matchDao.insertAll(any()) }
    }

    @Test
    fun `getMatchByNumber returns correct match`() = runTest {
        val entity = MatchEntity(
            matchNumber = 1,
            dateUtc = "2023-08-11T19:00:00Z",
            status = "FullTime",
            homeTeam = "Team A",
            awayTeam = "Team B",
            homeTeamScore = 2,
            awayTeamScore = 1,
            roundNumber = 1,
            location = "Stadium",
            group = null
        )

        coEvery { matchDao.getMatchByNumber(1) } returns entity

        val match = repository.getMatchByNumber(1)

        assertNotNull(match)
        assertEquals("Team A", match?.homeTeam)
        assertEquals("Team B", match?.awayTeam)
    }

    @Test
    fun `time conversion returns correct format`() = runTest {
        val entities = listOf(
            MatchEntity(
                matchNumber = 1,
                dateUtc = "2023-08-11T19:00:00Z",
                status = "FullTime",
                homeTeam = "Team A",
                awayTeam = "Team B",
                homeTeamScore = null,
                awayTeamScore = null,
                roundNumber = 1,
                location = "Stadium",
                group = null
            )
        )

        every { matchDao.getAllMatches() } returns flowOf(entities)

        val matches = repository.getAllMatches().first()

        assertEquals(1, matches.size)
        assertEquals("11.08.2023", matches[0].localDate)
        assertEquals("22:00", matches[0].localTime)
    }
}
