package com.ifthen.app.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ruleRepository: RuleRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun scheduleAllRules() {
        val rules = ruleRepository.getActiveRules()
        rules.forEach { rule ->
            scheduleRule(rule)
        }
    }

    fun scheduleRule(rule: Rule) {
        when (rule.triggerType) {
            TriggerType.TIME -> scheduleTimeRule(rule)
            TriggerType.CALENDAR -> scheduleCalendarCheckRule(rule)
            else -> {} // Pattern, Manual, Event don't need scheduling
        }
    }

    fun cancelRule(ruleId: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_RULE
            putExtra(EXTRA_RULE_ID, ruleId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ruleId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
        }
    }

    private fun scheduleTimeRule(rule: Rule) {
        val config = rule.triggerConfig as? TriggerConfig.TimeTrigger ?: return

        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val triggerTime = LocalTime.of(config.hour, config.minute)

        // Find the next valid trigger time
        var nextTrigger = today.atTime(triggerTime)
        if (nextTrigger.isBefore(now) || nextTrigger.isEqual(now)) {
            nextTrigger = nextTrigger.plusDays(1)
        }

        // Find next valid day
        while (nextTrigger.dayOfWeek.value !in config.daysOfWeek) {
            nextTrigger = nextTrigger.plusDays(1)
        }

        scheduleAlarm(rule.id, nextTrigger)
    }

    private fun scheduleCalendarCheckRule(rule: Rule) {
        val config = rule.triggerConfig as? TriggerConfig.CalendarTrigger ?: return

        val timeParts = config.checkTime.split(":")
        val hour = timeParts[0].toIntOrNull() ?: return
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val now = LocalDateTime.now()
        val today = now.toLocalDate()
        val triggerTime = LocalTime.of(hour, minute)

        var nextTrigger = today.atTime(triggerTime)
        if (nextTrigger.isBefore(now) || nextTrigger.isEqual(now)) {
            nextTrigger = nextTrigger.plusDays(1)
        }

        while (nextTrigger.dayOfWeek.value !in config.daysOfWeek) {
            nextTrigger = nextTrigger.plusDays(1)
        }

        scheduleAlarm(rule.id, nextTrigger)
    }

    private fun scheduleAlarm(ruleId: String, triggerTime: LocalDateTime) {
        val triggerMillis = triggerTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_RULE
            putExtra(EXTRA_RULE_ID, ruleId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ruleId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                // Fall back to inexact alarm
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    companion object {
        const val ACTION_TRIGGER_RULE = "com.ifthen.app.ACTION_TRIGGER_RULE"
        const val EXTRA_RULE_ID = "rule_id"
    }
}
