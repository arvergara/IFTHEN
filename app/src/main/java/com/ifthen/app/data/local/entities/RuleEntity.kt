package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.Priority
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: Category,
    val triggerType: TriggerType,
    val triggerConfigJson: String,  // JSON serialized TriggerConfig
    val ifCondition: String,        // "SI son las 7:35 y es dia de semana"
    val action: String,
    val durationMinutes: Int,
    val priority: Priority,
    val minimumAction: String?,
    val minimumDurationMinutes: Int?,
    val applicableModes: List<ModeType>,
    val isActive: Boolean,
    val createdAt: Long
)

fun RuleEntity.toDomain(triggerConfig: TriggerConfig): Rule = Rule(
    id = id,
    name = name,
    category = category,
    triggerType = triggerType,
    triggerConfig = triggerConfig,
    ifCondition = ifCondition,
    action = action,
    durationMinutes = durationMinutes,
    priority = priority,
    minimumAction = minimumAction,
    minimumDurationMinutes = minimumDurationMinutes,
    applicableModes = applicableModes,
    isActive = isActive,
    createdAt = createdAt
)

fun Rule.toEntity(triggerConfigJson: String): RuleEntity = RuleEntity(
    id = id,
    name = name,
    category = category,
    triggerType = triggerType,
    triggerConfigJson = triggerConfigJson,
    ifCondition = ifCondition,
    action = action,
    durationMinutes = durationMinutes,
    priority = priority,
    minimumAction = minimumAction,
    minimumDurationMinutes = minimumDurationMinutes,
    applicableModes = applicableModes,
    isActive = isActive,
    createdAt = createdAt
)
