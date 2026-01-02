package com.ifthen.app.domain.model

import java.util.UUID

enum class DelegateChannel {
    WHATSAPP,
    EMAIL;

    fun getDisplayName(): String = when (this) {
        WHATSAPP -> "WhatsApp"
        EMAIL -> "Email"
    }

    fun getEmoji(): String = when (this) {
        WHATSAPP -> "\uD83D\uDCAC" // 💬
        EMAIL -> "\u2709\uFE0F"     // ✉️
    }
}

data class Delegate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val channel: DelegateChannel,
    val contact: String, // Phone number or email
    val areas: List<String> = emptyList(), // Areas of expertise
    val createdAt: Long = System.currentTimeMillis()
)
