package com.example.eplmatches.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eplmatches.data.repository.MatchRepository
import com.example.eplmatches.domain.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchDetailUiState(
    val match: Match? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MatchDetailViewModel(
    private val matchNumber: Int,
    private val repository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState: StateFlow<MatchDetailUiState> = _uiState.asStateFlow()

    init {
        loadMatch()
    }

    private fun loadMatch() {
        viewModelScope.launch {
            _uiState.value = MatchDetailUiState(isLoading = true)
            try {
                val match = repository.getMatchByNumber(matchNumber)
                _uiState.value = MatchDetailUiState(match = match)
            } catch (e: Exception) {
                _uiState.value = MatchDetailUiState(error = "Failed to load match details")
            }
        }
    }

    class Factory(
        private val matchNumber: Int,
        private val repository: MatchRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MatchDetailViewModel(matchNumber, repository) as T
        }
    }
}
