package com.ifthen.app.domain.usecase

import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.RuleWithStatus
import com.ifthen.app.domain.model.TriggerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetTodayRulesUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val logRepository: LogRepository
) {

    operator fun invoke(): Flow<List<RuleWithStatus>> {
        val today = LocalDate.now()
        return combine(
            ruleRepository.getActiveRulesFlow(),
            logRepository.getLogsForDayFlow(today)
        ) { rules, logs ->
            val logsMap = logs.associateBy { it.ruleId }
            rules.map { rule ->
                RuleWithStatus(
                    rule = rule,
                    status = logsMap[rule.id]?.status
                )
            }.sortedWith(
                compareBy<RuleWithStatus> { it.status != null } // Pendientes primero
                    .thenBy { getTimeInMinutes(it.rule) } // Por hora del dia
            )
        }
    }

    private fun getTimeInMinutes(rule: Rule): Int {
        return when (val config = rule.triggerConfig) {
            is TriggerConfig.TimeTrigger -> config.hour * 60 + config.minute
            is TriggerConfig.CalendarTrigger -> {
                val parts = config.checkTime.split(":")
                if (parts.size == 2) {
                    parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0
                } else 0
            }
            else -> 1440 // Final del dia para otros tipos
        }
    }
}
