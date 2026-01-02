package com.ifthen.app.domain.model

import java.util.UUID

data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: Category,
    val triggerType: TriggerType,
    val triggerConfig: TriggerConfig,
    val ifCondition: String, // "SI son las 7:35 y estoy en modo RUTINA"
    val action: String,      // "ENTONCES medito 7 minutos"
    val durationMinutes: Int,
    val priority: Priority,
    val minimumAction: String? = null,
    val minimumDurationMinutes: Int? = null,
    val applicableModes: List<ModeType> = ModeType.entries.toList(),
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
