package com.ifthen.app.di

import android.content.Context
import com.ifthen.app.data.calendar.CalendarProvider
import com.ifthen.app.data.local.database.LogDao
import com.ifthen.app.data.local.database.ModeDao
import com.ifthen.app.data.local.database.RuleDao
import com.ifthen.app.data.local.database.StateDao
import com.ifthen.app.data.repository.CalendarRepository
import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.data.repository.RuleRepository
import com.ifthen.app.data.repository.UserModeRepository
import com.ifthen.app.data.repository.UserStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRuleRepository(ruleDao: RuleDao): RuleRepository = RuleRepository(ruleDao)

    @Provides
    @Singleton
    fun provideLogRepository(logDao: LogDao): LogRepository = LogRepository(logDao)

    @Provides
    @Singleton
    fun provideUserStateRepository(stateDao: StateDao): UserStateRepository = UserStateRepository(stateDao)

    @Provides
    @Singleton
    fun provideCalendarProvider(@ApplicationContext context: Context): CalendarProvider = CalendarProvider(context)

    @Provides
    @Singleton
    fun provideCalendarRepository(calendarProvider: CalendarProvider): CalendarRepository = CalendarRepository(calendarProvider)

    @Provides
    @Singleton
    fun provideUserModeRepository(modeDao: ModeDao): UserModeRepository = UserModeRepository(modeDao)
}
