package com.ifthen.app.domain.usecase

import com.ifthen.app.data.repository.CalendarRepository
import com.ifthen.app.domain.model.CalendarEvent
import java.time.LocalDateTime
import javax.inject.Inject

class CheckCalendarUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    fun isFreeUntil(until: LocalDateTime): Boolean =
        calendarRepository.isFreeUntil(LocalDateTime.now(), until)

    fun getNextMeeting(): CalendarEvent? = calendarRepository.getNextMeeting()

    fun getEventsToday(): List<CalendarEvent> {
        val now = LocalDateTime.now()
        val endOfDay = now.toLocalDate().plusDays(1).atStartOfDay()
        return calendarRepository.getEventsInRange(now, endOfDay)
    }
}
