package com.ifthen.app.domain.model

import java.util.UUID

data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: Category,
    val triggerType: TriggerType,
    val triggerConfig: TriggerConfig,
    val action: String,
    val durationMinutes: Int,
    val priority: Priority,
    val minimumAction: String? = null,
    val minimumDurationMinutes: Int? = null,
    val applicableModes: List<ModeType> = ModeType.entries.toList(), // Por defecto aplica en todos los modos
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class TriggeredRule(
    val rule: Rule,
    val useMinimum: Boolean
)

data class RuleWithStatus(
    val rule: Rule,
    val status: LogStatus?
)
