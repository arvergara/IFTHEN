package com.ifthen.app.domain.model

import java.time.LocalDateTime

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
