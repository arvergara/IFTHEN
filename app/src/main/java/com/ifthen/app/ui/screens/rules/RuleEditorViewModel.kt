package com.ifthen.app.ui.screens.rules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.Priority
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class RuleEditorUiState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: Category = Category.CUERPO,
    val triggerType: TriggerType = TriggerType.TIME,
    val ifCondition: String = "",
    val action: String = "",
    val durationMinutes: Int = 15,
    val priority: Priority = Priority.MEDIA,
    val minimumAction: String = "",
    val minimumDurationMinutes: Int = 5,
    val hour: Int = 8,
    val minute: Int = 0,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val eventName: String = "",
    val applicableModes: List<ModeType> = ModeType.entries.toList(),
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class RuleEditorViewModel @Inject constructor(
    private val ruleRepository: RuleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ruleId: String? = savedStateHandle["ruleId"]

    private val _uiState = MutableStateFlow(RuleEditorUiState())
    val uiState: StateFlow<RuleEditorUiState> = _uiState.asStateFlow()

    init {
        if (ruleId != null) {
            loadRule(ruleId)
        }
    }

    private fun loadRule(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val rule = ruleRepository.getRuleById(id)
            if (rule != null) {
                _uiState.update { state ->
                    state.copy(
                        id = rule.id,
                        name = rule.name,
                        category = rule.category,
                        triggerType = rule.triggerType,
                        ifCondition = rule.ifCondition,
                        action = rule.action,
                        durationMinutes = rule.durationMinutes,
                        priority = rule.priority,
                        minimumAction = rule.minimumAction ?: "",
                        minimumDurationMinutes = rule.minimumDurationMinutes ?: 5,
                        hour = extractHour(rule.triggerConfig),
                        minute = extractMinute(rule.triggerConfig),
                        daysOfWeek = extractDaysOfWeek(rule.triggerConfig),
                        eventName = extractEventName(rule.triggerConfig),
                        applicableModes = rule.applicableModes,
                        isEditing = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateCategory(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateTriggerType(triggerType: TriggerType) {
        _uiState.update { it.copy(triggerType = triggerType) }
    }

    fun updateIfCondition(ifCondition: String) {
        _uiState.update { it.copy(ifCondition = ifCondition) }
    }

    fun updateAction(action: String) {
        _uiState.update { it.copy(action = action) }
    }

    fun updateDuration(duration: Int) {
        _uiState.update { it.copy(durationMinutes = duration) }
    }

    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun updateMinimumAction(action: String) {
        _uiState.update { it.copy(minimumAction = action) }
    }

    fun updateMinimumDuration(duration: Int) {
        _uiState.update { it.copy(minimumDurationMinutes = duration) }
    }

    fun updateHour(hour: Int) {
        _uiState.update { it.copy(hour = hour) }
    }

    fun updateMinute(minute: Int) {
        _uiState.update { it.copy(minute = minute) }
    }

    fun updateDaysOfWeek(days: List<Int>) {
        _uiState.update { it.copy(daysOfWeek = days) }
    }

    fun updateEventName(eventName: String) {
        _uiState.update { it.copy(eventName = eventName) }
    }

    fun updateApplicableModes(modes: List<ModeType>) {
        _uiState.update { it.copy(applicableModes = modes) }
    }

    fun toggleMode(mode: ModeType) {
        _uiState.update { state ->
            val newModes = if (mode in state.applicableModes) {
                state.applicableModes - mode
            } else {
                state.applicableModes + mode
            }
            // Ensure at least one mode is selected
            if (newModes.isEmpty()) {
                state
            } else {
                state.copy(applicableModes = newModes)
            }
        }
    }

    fun saveRule() {
        viewModelScope.launch {
            val state = _uiState.value
            val triggerConfig = buildTriggerConfig(state)

            // Generate ifCondition if not provided
            val ifCondition = state.ifCondition.ifBlank {
                generateDefaultIfCondition(state)
            }

            val rule = Rule(
                id = state.id,
                name = state.name,
                category = state.category,
                triggerType = state.triggerType,
                triggerConfig = triggerConfig,
                ifCondition = ifCondition,
                action = state.action,
                durationMinutes = state.durationMinutes,
                priority = state.priority,
                minimumAction = state.minimumAction.takeIf { it.isNotBlank() },
                minimumDurationMinutes = if (state.minimumAction.isNotBlank()) state.minimumDurationMinutes else null,
                applicableModes = state.applicableModes,
                isActive = true
            )

            ruleRepository.insertRule(rule)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    private fun generateDefaultIfCondition(state: RuleEditorUiState): String {
        val time = String.format("%02d:%02d", state.hour, state.minute)
        return when (state.triggerType) {
            TriggerType.TIME -> "SI son las $time"
            TriggerType.CALENDAR -> "SI son las $time y tengo tiempo libre"
            TriggerType.EVENT -> "SI ocurre ${state.eventName}"
            TriggerType.MANUAL -> "SI activo manualmente"
            TriggerType.PATTERN -> "SI detecto el patron"
        }
    }

    private fun buildTriggerConfig(state: RuleEditorUiState): TriggerConfig {
        return when (state.triggerType) {
            TriggerType.TIME -> TriggerConfig.TimeTrigger(
                hour = state.hour,
                minute = state.minute,
                daysOfWeek = state.daysOfWeek
            )
            TriggerType.CALENDAR -> TriggerConfig.CalendarTrigger(
                checkTime = String.format("%02d:%02d", state.hour, state.minute),
                requiresFreeUntil = String.format("%02d:%02d", state.hour + 1, state.minute),
                daysOfWeek = state.daysOfWeek
            )
            TriggerType.PATTERN -> TriggerConfig.PatternTrigger(
                relatedRuleId = null,
                daysWithoutCompletion = 2,
                category = state.category
            )
            TriggerType.MANUAL -> TriggerConfig.ManualTrigger(
                stateRequired = null
            )
            TriggerType.EVENT -> TriggerConfig.EventTrigger(
                eventName = state.eventName
            )
        }
    }

    private fun extractHour(config: TriggerConfig): Int = when (config) {
        is TriggerConfig.TimeTrigger -> config.hour
        is TriggerConfig.CalendarTrigger -> config.checkTime.split(":").firstOrNull()?.toIntOrNull() ?: 8
        else -> 8
    }

    private fun extractMinute(config: TriggerConfig): Int = when (config) {
        is TriggerConfig.TimeTrigger -> config.minute
        is TriggerConfig.CalendarTrigger -> config.checkTime.split(":").getOrNull(1)?.toIntOrNull() ?: 0
        else -> 0
    }

    private fun extractDaysOfWeek(config: TriggerConfig): List<Int> = when (config) {
        is TriggerConfig.TimeTrigger -> config.daysOfWeek
        is TriggerConfig.CalendarTrigger -> config.daysOfWeek
        else -> listOf(1, 2, 3, 4, 5, 6, 7)
    }

    private fun extractEventName(config: TriggerConfig): String = when (config) {
        is TriggerConfig.EventTrigger -> config.eventName
        else -> ""
    }
}
