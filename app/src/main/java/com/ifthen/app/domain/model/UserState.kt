package com.ifthen.app.domain.model

import java.util.UUID

data class UserState(
    val id: String = UUID.randomUUID().toString(),
    val state: StateType,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)
