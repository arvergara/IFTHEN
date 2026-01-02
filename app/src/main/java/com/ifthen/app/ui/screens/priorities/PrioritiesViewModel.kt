package com.ifthen.app.ui.screens.priorities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.DailyPriorityRepository
import com.ifthen.app.domain.model.DailyPriority
import com.ifthen.app.domain.model.PriorityStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class PrioritiesUiState(
    val priorities: List<DailyPriority> = emptyList(),
    val isLoading: Boolean = true,
    val hasPrioritiesForToday: Boolean = false
)

@HiltViewModel
class PrioritiesViewModel @Inject constructor(
    private val dailyPriorityRepository: DailyPriorityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrioritiesUiState())
    val uiState: StateFlow<PrioritiesUiState> = _uiState

    init {
        loadTodayPriorities()
    }

    private fun loadTodayPriorities() {
        viewModelScope.launch {
            dailyPriorityRepository.getPrioritiesForDayFlow(LocalDate.now())
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
                .collect { priorities ->
                    _uiState.value = PrioritiesUiState(
                        priorities = priorities,
                        isLoading = false,
                        hasPrioritiesForToday = priorities.isNotEmpty()
                    )
                }
        }
    }

    fun savePriorities(priority1: String, priority2: String, priority3: String) {
        viewModelScope.launch {
            val priorities = listOf(priority1, priority2, priority3).filter { it.isNotBlank() }
            dailyPriorityRepository.savePriorities(LocalDate.now(), priorities)
        }
    }

    fun updatePriorityStatus(priorityId: String, status: PriorityStatus) {
        viewModelScope.launch {
            dailyPriorityRepository.updateStatus(priorityId, status)
        }
    }
}
