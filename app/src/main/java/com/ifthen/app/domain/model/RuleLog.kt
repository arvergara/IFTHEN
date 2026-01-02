package com.ifthen.app.domain.model

import java.util.UUID

data class RuleLog(
    val id: String = UUID.randomUUID().toString(),
    val ruleId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: LogStatus,
    val skipReason: SkipReason? = null,
    val wasMinimum: Boolean = false,
    val notes: String? = null
)
