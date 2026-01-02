package com.ifthen.app.domain.model

import java.time.LocalDate
import java.util.UUID

enum class PriorityStatus {
    PENDING,
    COMPLETED,
    DELEGATED;

    fun getDisplayName(): String = when (this) {
        PENDING -> "Pendiente"
        COMPLETED -> "Cumplida"
        DELEGATED -> "Delegada"
    }

    fun getEmoji(): String = when (this) {
        PENDING -> "\u23F3"      // ⏳
        COMPLETED -> "\u2705"    // ✅
        DELEGATED -> "\uD83D\uDCE4" // 📤
    }
}

data class DailyPriority(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val order: Int, // 1, 2, or 3
    val text: String,
    val status: PriorityStatus = PriorityStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
