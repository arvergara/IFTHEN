package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.DailyPriority
import com.ifthen.app.domain.model.PriorityStatus
import java.time.LocalDate

@Entity(tableName = "daily_priorities")
data class DailyPriorityEntity(
    @PrimaryKey
    val id: String,
    val dateEpochDay: Long, // LocalDate stored as epoch day
    val order: Int,
    val text: String,
    val status: PriorityStatus,
    val createdAt: Long
)

fun DailyPriorityEntity.toDomain(): DailyPriority = DailyPriority(
    id = id,
    date = LocalDate.ofEpochDay(dateEpochDay),
    order = order,
    text = text,
    status = status,
    createdAt = createdAt
)

fun DailyPriority.toEntity(): DailyPriorityEntity = DailyPriorityEntity(
    id = id,
    dateEpochDay = date.toEpochDay(),
    order = order,
    text = text,
    status = status,
    createdAt = createdAt
)
