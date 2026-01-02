package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.RuleLog
import com.ifthen.app.domain.model.SkipReason

@Entity(
    tableName = "logs",
    foreignKeys = [
        ForeignKey(
            entity = RuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ruleId"), Index("timestamp")]
)
data class LogEntity(
    @PrimaryKey
    val id: String,
    val ruleId: String,
    val timestamp: Long,
    val status: LogStatus,
    val skipReason: SkipReason?,
    val wasMinimum: Boolean,
    val notes: String?
)

fun LogEntity.toDomain(): RuleLog = RuleLog(
    id = id,
    ruleId = ruleId,
    timestamp = timestamp,
    status = status,
    skipReason = skipReason,
    wasMinimum = wasMinimum,
    notes = notes
)

fun RuleLog.toEntity(): LogEntity = LogEntity(
    id = id,
    ruleId = ruleId,
    timestamp = timestamp,
    status = status,
    skipReason = skipReason,
    wasMinimum = wasMinimum,
    notes = notes
)
