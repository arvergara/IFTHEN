package com.ifthen.app.domain.engine

import com.ifthen.app.data.repository.CalendarRepository
import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.data.repository.UserModeRepository
import com.ifthen.app.data.repository.UserStateRepository
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType
import com.ifthen.app.domain.model.TriggeredRule
import com.ifthen.app.domain.model.UserState
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class RuleEngine @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val logRepository: LogRepository,
    private val calendarRepository: CalendarRepository,
    private val userStateRepository: UserStateRepository,
    private val userModeRepository: UserModeRepository
) {

    /**
     * Evalúa todas las reglas activas y determina cuáles deben dispararse ahora
     */
    suspend fun evaluateRules(): List<TriggeredRule> {
        val activeRules = ruleRepository.getActiveRules()
        val currentState = userStateRepository.getCurrentState()
        val currentMode = userModeRepository.getCurrentMode().mode
        val triggeredRules = mutableListOf<TriggeredRule>()

        // Filtrar reglas que aplican al modo actual
        val applicableRules = activeRules.filter { rule ->
            currentMode in rule.applicableModes
        }

        for (rule in applicableRules) {
            val shouldTrigger = when (rule.triggerType) {
                TriggerType.TIME -> evaluateTimeTrigger(rule)
                TriggerType.CALENDAR -> evaluateCalendarTrigger(rule)
                TriggerType.PATTERN -> evaluatePatternTrigger(rule)
                TriggerType.MANUAL -> evaluateManualTrigger(rule, currentState)
                TriggerType.EVENT -> false // Se activa externamente
            }

            if (shouldTrigger) {
                val useMinimum = currentState.state in listOf(
                    StateType.DIA_PESADO,
                    StateType.CANSADO
                )
                triggeredRules.add(TriggeredRule(rule, useMinimum))
            }
        }

        return triggeredRules.sortedByDescending { it.rule.priority }
    }

    /**
     * Evalúa reglas que deberían dispararse para un evento específico
     */
    suspend fun evaluateEventTriggers(eventName: String): List<TriggeredRule> {
        val activeRules = ruleRepository.getActiveRules()
        val currentState = userStateRepository.getCurrentState()
        val currentMode = userModeRepository.getCurrentMode().mode
        val triggeredRules = mutableListOf<TriggeredRule>()

        // Filtrar reglas que aplican al modo actual
        val applicableRules = activeRules.filter { rule ->
            currentMode in rule.applicableModes
        }

        for (rule in applicableRules) {
            if (rule.triggerType == TriggerType.EVENT) {
                val config = rule.triggerConfig as? TriggerConfig.EventTrigger
                if (config?.eventName == eventName) {
                    val useMinimum = currentState.state in listOf(
                        StateType.DIA_PESADO,
                        StateType.CANSADO
                    )
                    triggeredRules.add(TriggeredRule(rule, useMinimum))
                }
            }
        }

        return triggeredRules.sortedByDescending { it.rule.priority }
    }

    private fun evaluateTimeTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.TimeTrigger ?: return false
        val now = LocalDateTime.now()
        return now.hour == config.hour &&
                now.minute == config.minute &&
                now.dayOfWeek.value in config.daysOfWeek
    }

    private fun evaluateCalendarTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.CalendarTrigger ?: return false
        val now = LocalDateTime.now()

        val checkTimeParts = config.checkTime.split(":")
        val checkTime = LocalTime.of(checkTimeParts[0].toInt(), checkTimeParts[1].toInt())

        if (now.toLocalTime().hour != checkTime.hour || now.toLocalTime().minute != checkTime.minute) {
            return false
        }
        if (now.dayOfWeek.value !in config.daysOfWeek) {
            return false
        }

        // Verificar que no hay reuniones hasta la hora indicada
        val freeUntilParts = config.requiresFreeUntil.split(":")
        val freeUntil = LocalTime.of(freeUntilParts[0].toInt(), freeUntilParts[1].toInt())
        return calendarRepository.isFreeUntil(
            from = now,
            until = now.toLocalDate().atTime(freeUntil)
        )
    }

    private suspend fun evaluatePatternTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.PatternTrigger ?: return false

        val daysToCheck = config.daysWithoutCompletion
        if (daysToCheck <= 0) return false

        val logs = if (config.relatedRuleId != null) {
            logRepository.getLogsForRuleDays(config.relatedRuleId, daysToCheck)
        } else if (config.category != null) {
            logRepository.getLogsForCategoryDays(config.category, daysToCheck)
        } else {
            return false
        }

        // Si no hay logs completados en los últimos N días, disparar
        val completedLogs = logs.filter {
            it.status == LogStatus.COMPLETED || it.status == LogStatus.MINIMUM
        }
        return completedLogs.isEmpty()
    }

    private fun evaluateManualTrigger(rule: Rule, currentState: UserState): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.ManualTrigger ?: return false
        return config.stateRequired == null || currentState.state == config.stateRequired
    }
}
