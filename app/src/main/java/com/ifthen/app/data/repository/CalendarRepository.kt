package com.ifthen.app.data.repository

import com.ifthen.app.data.calendar.CalendarProvider
import com.ifthen.app.domain.model.CalendarEvent
import java.time.LocalDateTime
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val calendarProvider: CalendarProvider
) {

    /**
     * Verifica si el usuario está libre desde 'from' hasta 'until'
     */
    fun isFreeUntil(from: LocalDateTime, until: LocalDateTime): Boolean {
        val events = calendarProvider.getEventsInRange(from, until)
        return events.isEmpty()
    }

    /**
     * Obtiene la próxima reunión del día
     */
    fun getNextMeeting(): CalendarEvent? = calendarProvider.getNextEvent()

    /**
     * Obtiene todos los eventos en un rango
     */
    fun getEventsInRange(from: LocalDateTime, until: LocalDateTime): List<CalendarEvent> =
        calendarProvider.getEventsInRange(from, until)
}
