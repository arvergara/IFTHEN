package com.ifthen.app.domain.model

import java.util.UUID

data class UserMode(
    val id: String = UUID.randomUUID().toString(),
    val mode: ModeType,
    val timestamp: Long = System.currentTimeMillis()
)
