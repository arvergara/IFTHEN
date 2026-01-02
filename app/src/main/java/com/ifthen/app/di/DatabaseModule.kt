package com.ifthen.app.di

import android.content.Context
import androidx.room.Room
import com.ifthen.app.data.local.database.AppDatabase
import com.ifthen.app.data.local.database.DailyPriorityDao
import com.ifthen.app.data.local.database.DelegateDao
import com.ifthen.app.data.local.database.LogDao
import com.ifthen.app.data.local.database.ModeDao
import com.ifthen.app.data.local.database.RuleDao
import com.ifthen.app.data.local.database.StateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ifthen_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRuleDao(database: AppDatabase): RuleDao = database.ruleDao()

    @Provides
    @Singleton
    fun provideLogDao(database: AppDatabase): LogDao = database.logDao()

    @Provides
    @Singleton
    fun provideStateDao(database: AppDatabase): StateDao = database.stateDao()

    @Provides
    @Singleton
    fun provideModeDao(database: AppDatabase): ModeDao = database.modeDao()

    @Provides
    @Singleton
    fun provideDelegateDao(database: AppDatabase): DelegateDao = database.delegateDao()

    @Provides
    @Singleton
    fun provideDailyPriorityDao(database: AppDatabase): DailyPriorityDao = database.dailyPriorityDao()
}
