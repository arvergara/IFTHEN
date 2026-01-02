package com.ifthen.app.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ifthen.app.IFThenApplication.Companion.CHANNEL_RULES
import com.ifthen.app.MainActivity
import com.ifthen.app.R
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.TriggeredRule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RuleNotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun buildRuleNotification(triggeredRule: TriggeredRule): Notification {
        val rule = triggeredRule.rule
        val useMinimum = triggeredRule.useMinimum

        val action = if (useMinimum && rule.minimumAction != null) {
            rule.minimumAction
        } else {
            rule.action
        }

        val duration = if (useMinimum && rule.minimumDurationMinutes != null) {
            rule.minimumDurationMinutes
        } else {
            rule.durationMinutes
        }

        val categoryEmoji = rule.category.getEmoji()
        val title = "$categoryEmoji ${rule.ifCondition}"
        val text = "→ $action ($duration min)"

        // Intent para abrir la app
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Acciones de la notificación
        val doneIntent = createActionIntent(rule.id, NotificationAction.DONE)
        val skipIntent = createActionIntent(rule.id, NotificationAction.SKIP)
        val minimumIntent = createActionIntent(rule.id, NotificationAction.MINIMUM)

        return NotificationCompat.Builder(context, CHANNEL_RULES)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setContentIntent(contentIntent)
            .addAction(android.R.drawable.ic_menu_send, "HECHO", doneIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "SALTAR", skipIntent)
            .apply {
                if (!useMinimum && rule.minimumAction != null && rule.minimumDurationMinutes != null) {
                    addAction(
                        android.R.drawable.ic_menu_revert,
                        "MINIMO ${rule.minimumDurationMinutes} MIN",
                        minimumIntent
                    )
                }
            }
            .build()
    }

    private fun createActionIntent(ruleId: String, action: NotificationAction): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = "com.ifthen.app.NOTIFICATION_ACTION"
            putExtra(EXTRA_RULE_ID, ruleId)
            putExtra(EXTRA_ACTION, action.name)
        }

        val requestCode = "${ruleId}_${action.name}".hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val EXTRA_RULE_ID = "rule_id"
        const val EXTRA_ACTION = "action"
    }
}
