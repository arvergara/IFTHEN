package com.ifthen.app.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.data.repository.UserModeRepository
import com.ifthen.app.data.repository.UserStateRepository
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType
import com.ifthen.app.domain.model.TriggeredRule
import com.ifthen.app.notification.RuleNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var ruleRepository: RuleRepository

    @Inject
    lateinit var userStateRepository: UserStateRepository

    @Inject
    lateinit var userModeRepository: UserModeRepository

    @Inject
    lateinit var notificationManager: RuleNotificationManager

    @Inject
    lateinit var ruleScheduler: RuleScheduler

    @Inject
    lateinit var calendarChecker: CalendarChecker

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RuleScheduler.ACTION_TRIGGER_RULE -> {
                val ruleId = intent.getStringExtra(RuleScheduler.EXTRA_RULE_ID) ?: return
                handleRuleTrigger(ruleId)
            }
        }
    }

    private fun handleRuleTrigger(ruleId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val rule = ruleRepository.getRuleById(ruleId) ?: return@launch
            val currentState = userStateRepository.getCurrentState()
            val currentMode = userModeRepository.getCurrentMode()

            // Check if rule applies to current mode
            if (currentMode.mode !in rule.applicableModes) {
                // Skip this rule, just reschedule
                ruleScheduler.scheduleRule(rule)
                return@launch
            }

            // For calendar triggers, check if calendar is free
            if (rule.triggerType == TriggerType.CALENDAR) {
                val config = rule.triggerConfig as? TriggerConfig.CalendarTrigger
                if (config != null && !calendarChecker.isCalendarFreeUntil(config.requiresFreeUntil)) {
                    // Calendar is busy, skip this notification
                    ruleScheduler.scheduleRule(rule)
                    return@launch
                }
            }

            val useMinimum = currentState.state in listOf(
                StateType.DIA_PESADO,
                StateType.CANSADO
            )

            val triggeredRule = TriggeredRule(rule, useMinimum)
            notificationManager.showRuleNotification(triggeredRule)

            // Reschedule for next occurrence
            ruleScheduler.scheduleRule(rule)
        }
    }
}
