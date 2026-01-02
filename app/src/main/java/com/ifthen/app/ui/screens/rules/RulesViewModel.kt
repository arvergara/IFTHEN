package com.ifthen.app.ui.screens.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.Rule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RulesUiState(
    val rules: List<Rule> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val ruleRepository: RuleRepository
) : ViewModel() {

    val uiState: StateFlow<RulesUiState> = ruleRepository.getAllRules()
        .map { rules ->
            RulesUiState(
                rules = rules,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RulesUiState()
        )

    fun toggleRuleActive(ruleId: String, isActive: Boolean) {
        viewModelScope.launch {
            ruleRepository.setRuleActive(ruleId, isActive)
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            ruleRepository.deleteRuleById(ruleId)
        }
    }
}
