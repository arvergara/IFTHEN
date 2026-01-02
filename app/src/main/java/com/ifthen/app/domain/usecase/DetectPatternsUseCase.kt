package com.ifthen.app.domain.usecase

import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.PatternSuggestion
import com.ifthen.app.domain.model.RuleLog
import com.ifthen.app.domain.model.SuggestionType
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class DetectPatternsUseCase @Inject constructor(
    private val logRepository: LogRepository,
    private val ruleRepository: RuleRepository
) {

    suspend operator fun invoke(): List<PatternSuggestion> {
        val suggestions = mutableListOf<PatternSuggestion>()
        val rules = ruleRepository.getActiveRules()

        for (rule in rules) {
            val logs = logRepository.getLogsForRuleDays(rule.id, days = 14)

            // Detectar regla que falla consistentemente a cierto día
            val failuresByDay = logs
                .filter { it.status == LogStatus.SKIPPED || it.status == LogStatus.MISSED }
                .groupBy { getDayOfWeek(it.timestamp) }

            for ((day, failures) in failuresByDay) {
                if (failures.size >= 3) {
                    val dayName = day.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                    suggestions.add(
                        PatternSuggestion(
                            ruleId = rule.id,
                            type = SuggestionType.CHANGE_DAY,
                            message = "\"${rule.name}\" falla ${failures.size} veces los $dayName. ¿Mover a otro día u hora?",
                            suggestedChange = "Cambiar día o reducir duración"
                        )
                    )
                }
            }

            // Detectar si versión mínima se usa mucho
            val minimumCount = logs.count { it.wasMinimum }
            val totalCount = logs.size
            if (totalCount > 5 && minimumCount.toFloat() / totalCount > 0.6f) {
                suggestions.add(
                    PatternSuggestion(
                        ruleId = rule.id,
                        type = SuggestionType.REDUCE_DEFAULT,
                        message = "Usas el mínimo en \"${rule.name}\" el ${(minimumCount * 100 / totalCount)}% del tiempo. ¿Hacer el mínimo el nuevo default?",
                        suggestedChange = "Reducir duración default a ${rule.minimumDurationMinutes} min"
                    )
                )
            }

            // Detectar regla que casi nunca se completa
            val completedCount = logs.count { it.status == LogStatus.COMPLETED || it.status == LogStatus.MINIMUM }
            if (totalCount > 7 && completedCount.toFloat() / totalCount < 0.2f) {
                suggestions.add(
                    PatternSuggestion(
                        ruleId = rule.id,
                        type = SuggestionType.DEACTIVATE,
                        message = "\"${rule.name}\" se completa solo ${(completedCount * 100 / totalCount)}% del tiempo. ¿Desactivar o ajustar?",
                        suggestedChange = "Considerar desactivar esta regla"
                    )
                )
            }
        }

        return suggestions
    }

    private fun getDayOfWeek(timestamp: Long): DayOfWeek {
        val date = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return date.dayOfWeek
    }
}
