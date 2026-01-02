package com.ifthen.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ifthen.app.data.local.entities.DailyPriorityEntity
import com.ifthen.app.data.local.entities.DelegateEntity
import com.ifthen.app.data.local.entities.LogEntity
import com.ifthen.app.data.local.entities.RuleEntity
import com.ifthen.app.data.local.entities.UserModeEntity
import com.ifthen.app.data.local.entities.UserStateEntity

@Database(
    entities = [
        RuleEntity::class,
        LogEntity::class,
        UserStateEntity::class,
        UserModeEntity::class,
        DelegateEntity::class,
        DailyPriorityEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ruleDao(): RuleDao
    abstract fun logDao(): LogDao
    abstract fun stateDao(): StateDao
    abstract fun modeDao(): ModeDao
    abstract fun delegateDao(): DelegateDao
    abstract fun dailyPriorityDao(): DailyPriorityDao
}
