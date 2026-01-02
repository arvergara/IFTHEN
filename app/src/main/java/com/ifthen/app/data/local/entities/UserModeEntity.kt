package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.UserMode

@Entity(tableName = "user_modes")
data class UserModeEntity(
    @PrimaryKey
    val id: String,
    val mode: ModeType,
    val timestamp: Long
)

fun UserModeEntity.toDomain(): UserMode = UserMode(
    id = id,
    mode = mode,
    timestamp = timestamp
)

fun UserMode.toEntity(): UserModeEntity = UserModeEntity(
    id = id,
    mode = mode,
    timestamp = timestamp
)
