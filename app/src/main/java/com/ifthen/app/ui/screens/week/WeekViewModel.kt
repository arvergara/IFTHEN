package com.ifthen.app.ui.screens.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.DailyPriorityRepository
import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.PatternSuggestion
import com.ifthen.app.domain.model.PriorityStatus
import com.ifthen.app.domain.usecase.DetectPatternsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class DayStats(
    val name: String,
    val percentage: Int
)

data class PriorityStats(
    val total: Int,
    val completed: Int,
    val delegated: Int,
    val pending: Int
)

data class WeekUiState(
    val completionPercentage: Int = 0,
    val worstDay: DayStats? = null,
    val suggestions: List<PatternSuggestion> = emptyList(),
    val dailyStats: List<DayStats> = emptyList(),
    val alcoholCount: Int = 0,
    val priorityStats: PriorityStats = PriorityStats(0, 0, 0, 0),
    val isLoading: Boolean = true
)

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val logRepository: LogRepository,
    private val ruleRepository: RuleRepository,
    private val detectPatternsUseCase: DetectPatternsUseCase,
    private val dailyPriorityRepository: DailyPriorityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeekUiState())
    val uiState: StateFlow<WeekUiState> = _uiState.asStateFlow()

    init {
        loadWeekData()
    }

    fun refresh() {
        loadWeekData()
    }

    private fun loadWeekData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val today = LocalDate.now()
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            val endOfWeek = startOfWeek.plusDays(6)

            // Get all logs for the week
            val logs = logRepository.getLogsInRange(startOfWeek, endOfWeek)
            val rules = ruleRepository.getActiveRules()
            val totalRulesPerDay = rules.size

            // Calculate daily stats
            val dailyStats = mutableListOf<DayStats>()
            var totalCompleted = 0
            var totalExpected = 0

            for (i in 0..6) {
                val date = startOfWeek.plusDays(i.toLong())
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))

                if (date <= today) {
                    val dayLogs = logs.filter { log ->
                        val logDate = java.time.Instant.ofEpochMilli(log.timestamp)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        logDate == date
                    }

                    val completed = dayLogs.count {
                        it.status == LogStatus.COMPLETED || it.status == LogStatus.MINIMUM
                    }

                    val percentage = if (totalRulesPerDay > 0) {
                        (completed * 100) / totalRulesPerDay
                    } else {
                        0
                    }

                    dailyStats.add(DayStats(dayName, percentage))
                    totalCompleted += completed
                    totalExpected += totalRulesPerDay
                } else {
                    dailyStats.add(DayStats(dayName, -1)) // Future day
                }
            }

            val completionPercentage = if (totalExpected > 0) {
                (totalCompleted * 100) / totalExpected
            } else {
                0
            }

            // Find worst day
            val worstDay = dailyStats
                .filter { it.percentage >= 0 }
                .minByOrNull { it.percentage }
                ?.takeIf { it.percentage < 50 }

            // Count alcohol days (skipped = drank alcohol)
            val alcoholCount = logs.count { log ->
                log.ruleId == "rule_cuerpo_alcohol" && log.status == LogStatus.SKIPPED
            }

            // Get priority stats
            val priorities = dailyPriorityRepository.getPrioritiesInRange(startOfWeek, endOfWeek)
            val priorityStats = PriorityStats(
                total = priorities.size,
                completed = priorities.count { it.status == PriorityStatus.COMPLETED },
                delegated = priorities.count { it.status == PriorityStatus.DELEGATED },
                pending = priorities.count { it.status == PriorityStatus.PENDING }
            )

            // Get pattern suggestions
            val suggestions = detectPatternsUseCase()

            _uiState.update {
                it.copy(
                    completionPercentage = completionPercentage,
                    worstDay = worstDay,
                    suggestions = suggestions,
                    dailyStats = dailyStats,
                    alcoholCount = alcoholCount,
                    priorityStats = priorityStats,
                    isLoading = false
                )
            }
        }
    }

    fun applySuggestion(suggestion: PatternSuggestion) {
        // For now, just dismiss the suggestion
        // In a full implementation, this would navigate to rule editor or apply changes
        dismissSuggestion(suggestion)
    }

    fun dismissSuggestion(suggestion: PatternSuggestion) {
        _uiState.update { state ->
            state.copy(suggestions = state.suggestions.filter { it != suggestion })
        }
    }
}
