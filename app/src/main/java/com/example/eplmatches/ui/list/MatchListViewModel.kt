package com.example.eplmatches.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eplmatches.data.repository.MatchRepository
import com.example.eplmatches.domain.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MatchListUiState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val hasMorePages: Boolean = true
)

private const val PAGE_SIZE = 20

class MatchListViewModel(
    private val repository: MatchRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)

    private val _currentPage = MutableStateFlow(0)

    private val _allMatches = MutableStateFlow<List<Match>>(emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val filteredMatches: StateFlow<List<Match>> = combine(
        _allMatches, _searchQuery
    ) { matches, query ->
        if (query.isBlank()) matches
        else matches.filter {
            it.homeTeam.contains(query, ignoreCase = true) ||
                    it.awayTeam.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val uiState: StateFlow<MatchListUiState> = combine(
        filteredMatches, _isRefreshing, _currentPage, _error
    ) { matches, refreshing, page, err ->
        val endIndex = minOf((page + 1) * PAGE_SIZE, matches.size)
        val pagedMatches = if (matches.isNotEmpty()) {
            matches.subList(0, endIndex)
        } else {
            emptyList()
        }
        MatchListUiState(
            matches = pagedMatches,
            isLoading = false,
            isRefreshing = refreshing,
            error = err,
            searchQuery = _searchQuery.value,
            hasMorePages = endIndex < matches.size
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MatchListUiState())

    init {
        observeCachedData()
        loadMatches()
    }

    private fun observeCachedData() {
        viewModelScope.launch {
            repository.getAllMatches().collect { matches ->
                _allMatches.value = matches
            }
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            try {
                repository.refreshMatches()
                _currentPage.value = 0
            } catch (e: Exception) {
                if (_allMatches.value.isEmpty()) {
                    _error.value = "Failed to load matches: ${e.localizedMessage}"
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _currentPage.value = 0
    }

    fun loadNextPage() {
        val current = _currentPage.value
        val totalFiltered = filteredMatches.value.size
        if ((current + 1) * PAGE_SIZE < totalFiltered) {
            _currentPage.value = current + 1
        }
    }

    fun retry() {
        loadMatches()
    }

    class Factory(private val repository: MatchRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MatchListViewModel(repository) as T
        }
    }
}
