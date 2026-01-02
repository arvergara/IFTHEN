package com.ifthen.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ifthen.app.data.DefaultRules
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.scheduler.RuleScheduler
import com.ifthen.app.scheduler.RuleWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class IFThenApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var ruleRepository: RuleRepository

    @Inject
    lateinit var ruleScheduler: RuleScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        initializeApp()
    }

    private fun initializeApp() {
        CoroutineScope(Dispatchers.IO).launch {
            // Seed default rules if database is empty
            val rulesCount = ruleRepository.getRulesCount()
            if (rulesCount == 0) {
                val defaultRules = DefaultRules.getDefaultRules()
                ruleRepository.insertRules(defaultRules)
            }

            // Schedule all rules
            ruleScheduler.scheduleAllRules()

            // Start periodic worker for pattern evaluation
            RuleWorker.schedule(this@IFThenApplication)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_RULES,
                getString(R.string.notification_channel_rules),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_description)
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_RULES = "rules_channel"
    }
}
