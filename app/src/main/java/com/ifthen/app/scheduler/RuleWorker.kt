package com.ifthen.app.scheduler

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ifthen.app.domain.engine.RuleEngine
import com.ifthen.app.notification.RuleNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class RuleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val ruleEngine: RuleEngine,
    private val notificationManager: RuleNotificationManager,
    private val ruleScheduler: RuleScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Evaluate pattern triggers
            val triggeredRules = ruleEngine.evaluateRules()

            // Show notifications for triggered rules
            triggeredRules.forEach { triggeredRule ->
                notificationManager.showRuleNotification(triggeredRule)
            }

            // Reschedule all rules to ensure alarms are up to date
            ruleScheduler.scheduleAllRules()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "rule_evaluation_work"

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<RuleWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
