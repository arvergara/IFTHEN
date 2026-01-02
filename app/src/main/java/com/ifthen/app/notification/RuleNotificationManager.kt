package com.ifthen.app.notification

import android.app.NotificationManager
import android.content.Context
import com.ifthen.app.domain.model.TriggeredRule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationBuilder: RuleNotificationBuilder
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showRuleNotification(triggeredRule: TriggeredRule) {
        val notification = notificationBuilder.buildRuleNotification(triggeredRule)
        val notificationId = triggeredRule.rule.id.hashCode()
        notificationManager.notify(notificationId, notification)
    }

    fun cancelRuleNotification(ruleId: String) {
        notificationManager.cancel(ruleId.hashCode())
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
