package com.ifthen.app.data.calendar

import android.content.ContentResolver
import android.content.Context
import android.provider.CalendarContract
import com.ifthen.app.domain.model.CalendarEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class CalendarProvider @Inject constructor(
    private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun getEventsInRange(from: LocalDateTime, until: LocalDateTime): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        val fromMillis = from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val untilMillis = until.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} < ?"
        val selectionArgs = arrayOf(fromMillis.toString(), untilMillis.toString())

        try {
            contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(CalendarContract.Events._ID)
                val titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE)
                val startIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART)
                val endIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val title = cursor.getString(titleIndex) ?: ""
                    val startMillis = cursor.getLong(startIndex)
                    val endMillis = cursor.getLong(endIndex)

                    val startTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(startMillis),
                        ZoneId.systemDefault()
                    )
                    val endTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(endMillis),
                        ZoneId.systemDefault()
                    )

                    events.add(
                        CalendarEvent(
                            id = id,
                            title = title,
                            startTime = startTime,
                            endTime = endTime
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Calendar permission not granted
            return emptyList()
        }

        return events
    }

    fun getNextEvent(): CalendarEvent? {
        val now = LocalDateTime.now()
        val endOfDay = now.toLocalDate().plusDays(1).atStartOfDay()
        return getEventsInRange(now, endOfDay).firstOrNull()
    }
}
