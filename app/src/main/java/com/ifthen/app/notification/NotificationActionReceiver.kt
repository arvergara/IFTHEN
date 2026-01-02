package com.ifthen.app.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.SkipReason
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var logRepository: LogRepository

    override fun onReceive(context: Context, intent: Intent) {
        val ruleId = intent.getStringExtra(RuleNotificationBuilder.EXTRA_RULE_ID) ?: return
        val actionName = intent.getStringExtra(RuleNotificationBuilder.EXTRA_ACTION) ?: return
        val action = try {
            NotificationAction.valueOf(actionName)
        } catch (e: IllegalArgumentException) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                NotificationAction.DONE -> {
                    logRepository.logCompletion(ruleId, LogStatus.COMPLETED)
                    cancelNotification(context, ruleId)
                }
                NotificationAction.SKIP -> {
                    // Por ahora, saltar con razón "DIA_PESADO" por defecto
                    // En una versión completa, se mostraría un diálogo
                    logRepository.logCompletion(
                        ruleId = ruleId,
                        status = LogStatus.SKIPPED,
                        skipReason = SkipReason.DIA_PESADO
                    )
                    cancelNotification(context, ruleId)
                }
                NotificationAction.MINIMUM -> {
                    logRepository.logCompletion(
                        ruleId = ruleId,
                        status = LogStatus.MINIMUM,
                        wasMinimum = true
                    )
                    cancelNotification(context, ruleId)
                }
            }
        }
    }

    private fun cancelNotification(context: Context, ruleId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ruleId.hashCode())
    }
}
