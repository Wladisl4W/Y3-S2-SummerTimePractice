package com.example.eplmatches

import com.example.eplmatches.data.repository.MatchRepository
import com.example.eplmatches.domain.model.Match
import com.example.eplmatches.ui.list.MatchListViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchListViewModelTest {

    private lateinit var repository: MatchRepository
    private lateinit var viewModel: MatchListViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testMatches = listOf(
        Match(
            matchNumber = 1,
            dateUtc = "2023-08-11T19:00:00Z",
            status = "FullTime",
            homeTeam = "Arsenal",
            awayTeam = "Chelsea",
            homeTeamScore = 2,
            awayTeamScore = 1,
            roundNumber = 1,
            location = "Emirates Stadium",
            group = null,
            localDate = "11.08.2023",
            localTime = "22:00"
        ),
        Match(
            matchNumber = 2,
            dateUtc = "2023-08-12T14:00:00Z",
            status = "FullTime",
            homeTeam = "Liverpool",
            awayTeam = "Manchester United",
            homeTeamScore = 3,
            awayTeamScore = 0,
            roundNumber = 1,
            location = "Anfield",
            group = null,
            localDate = "12.08.2023",
            localTime = "17:00"
        ),
        Match(
            matchNumber = 3,
            dateUtc = "2023-08-12T16:30:00Z",
            status = "Upcoming",
            homeTeam = "Manchester City",
            awayTeam = "Tottenham",
            homeTeamScore = null,
            awayTeamScore = null,
            roundNumber = 1,
            location = "Etihad Stadium",
            group = null,
            localDate = "12.08.2023",
            localTime = "19:30"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.getAllMatches() } returns MutableStateFlow(emptyList())
        viewModel = MatchListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty matches`() {
        val state = viewModel.uiState.value
        assertTrue(state.matches.isEmpty())
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `search query filters matches by team name`() = runTest {
        every { repository.getAllMatches() } returns MutableStateFlow(testMatches)

        viewModel = MatchListViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Arsenal")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.matches.size)
        assertEquals("Arsenal", state.matches[0].homeTeam)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        every { repository.getAllMatches() } returns MutableStateFlow(testMatches)

        viewModel = MatchListViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("liverpool")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.matches.size)
        assertEquals("Liverpool", state.matches[0].homeTeam)
    }

    @Test
    fun `empty search shows all matches`() = runTest {
        every { repository.getAllMatches() } returns MutableStateFlow(testMatches)

        viewModel = MatchListViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.matches.size)
    }
}
