package com.ifthen.app.scheduler

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Checks if the calendar is free from now until the specified time.
     * @param untilTime Time string in "HH:mm" format (e.g., "08:15")
     * @return true if calendar is free, false if there's an event or no permission
     */
    fun isCalendarFreeUntil(untilTime: String): Boolean {
        if (!hasCalendarPermission()) {
            return true // If no permission, assume free (don't block the rule)
        }

        val timeParts = untilTime.split(":")
        val hour = timeParts[0].toIntOrNull() ?: return true
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = System.currentTimeMillis()
        val today = LocalDate.now()
        val endTime = today.atTime(LocalTime.of(hour, minute))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // If the end time has already passed, return false
        if (endTime <= now) {
            return false
        }

        return !hasEventsInRange(now, endTime)
    }

    private fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasEventsInRange(startMillis: Long, endMillis: Long): Boolean {
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        // Query for events that overlap with our time range
        val selection = """
            (${CalendarContract.Events.DTSTART} < ? AND ${CalendarContract.Events.DTEND} > ?)
            OR (${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} < ?)
        """.trimIndent()

        val selectionArgs = arrayOf(
            endMillis.toString(),
            startMillis.toString(),
            startMillis.toString(),
            endMillis.toString()
        )

        return try {
            val cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )

            val hasEvents = cursor?.use { it.count > 0 } ?: false
            hasEvents
        } catch (e: SecurityException) {
            false // If there's a security exception, assume no events
        }
    }
}
