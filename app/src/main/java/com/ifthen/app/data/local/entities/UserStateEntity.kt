package com.ifthen.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.model.UserState

@Entity(tableName = "user_states")
data class UserStateEntity(
    @PrimaryKey
    val id: String,
    val state: StateType,
    val timestamp: Long,
    val expiresAt: Long?
)

fun UserStateEntity.toDomain(): UserState = UserState(
    id = id,
    state = state,
    timestamp = timestamp,
    expiresAt = expiresAt
)

fun UserState.toEntity(): UserStateEntity = UserStateEntity(
    id = id,
    state = state,
    timestamp = timestamp,
    expiresAt = expiresAt
)
