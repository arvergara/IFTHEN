package com.ifthen.app.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.DailyPriorityRepository
import com.ifthen.app.data.repository.UserModeRepository
import com.ifthen.app.data.repository.UserStateRepository
import com.ifthen.app.domain.model.DailyPriority
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.RuleWithStatus
import com.ifthen.app.domain.model.SkipReason
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.usecase.GetTodayRulesUseCase
import com.ifthen.app.domain.usecase.LogRuleCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class TodayUiState(
    val dateFormatted: String = "",
    val currentState: StateType = StateType.NORMAL,
    val currentMode: ModeType = ModeType.RUTINA,
    val todayRules: List<RuleWithStatus> = emptyList(),
    val todayPriorities: List<DailyPriority> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val getTodayRulesUseCase: GetTodayRulesUseCase,
    private val logRuleCompletionUseCase: LogRuleCompletionUseCase,
    private val userStateRepository: UserStateRepository,
    private val userModeRepository: UserModeRepository,
    private val dailyPriorityRepository: DailyPriorityRepository
) : ViewModel() {

    private val _currentState = MutableStateFlow(StateType.NORMAL)

    val uiState: StateFlow<TodayUiState> = combine(
        getTodayRulesUseCase(),
        userStateRepository.getCurrentStateFlow(),
        userModeRepository.getCurrentModeFlow(),
        dailyPriorityRepository.getPrioritiesForDayFlow(LocalDate.now())
    ) { rules, userState, userMode, priorities ->
        TodayUiState(
            dateFormatted = formatDate(LocalDate.now()),
            currentState = userState.state,
            currentMode = userMode.mode,
            todayRules = rules,
            todayPriorities = priorities,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState(dateFormatted = formatDate(LocalDate.now()))
    )

    fun updateState(state: StateType) {
        viewModelScope.launch {
            userStateRepository.setState(state)
            _currentState.value = state
        }
    }

    fun updateMode(mode: ModeType) {
        viewModelScope.launch {
            userModeRepository.setMode(mode)
        }
    }

    fun markComplete(ruleId: String) {
        viewModelScope.launch {
            logRuleCompletionUseCase.complete(ruleId)
        }
    }

    fun markMinimum(ruleId: String) {
        viewModelScope.launch {
            logRuleCompletionUseCase.completeMinimum(ruleId)
        }
    }

    fun markSkipped(ruleId: String, reason: SkipReason = SkipReason.DIA_PESADO) {
        viewModelScope.launch {
            logRuleCompletionUseCase.skip(ruleId, reason)
        }
    }

    fun savePriorities(p1: String, p2: String, p3: String) {
        viewModelScope.launch {
            dailyPriorityRepository.savePriorities(
                LocalDate.now(),
                listOf(p1, p2, p3).filter { it.isNotBlank() }
            )
        }
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("E dd-MMM-yy", Locale("es", "ES"))
        return date.format(formatter).replaceFirstChar { it.titlecase(Locale("es", "ES")) }
    }
}
