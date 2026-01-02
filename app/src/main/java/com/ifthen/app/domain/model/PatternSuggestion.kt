package com.ifthen.app.domain.model

data class PatternSuggestion(
    val ruleId: String,
    val type: SuggestionType,
    val message: String,
    val suggestedChange: String
)

enum class SuggestionType {
    CHANGE_DAY,
    CHANGE_TIME,
    REDUCE_DEFAULT,
    DEACTIVATE
}
