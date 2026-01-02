package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.LogDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.RuleLog
import com.ifthen.app.domain.model.SkipReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class LogRepository @Inject constructor(
    private val logDao: LogDao
) {

    fun getAllLogs(): Flow<List<RuleLog>> = logDao.getAllLogs().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getLogsForRule(ruleId: String): Flow<List<RuleLog>> =
        logDao.getLogsForRule(ruleId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getLogsForRuleDays(ruleId: String, days: Int): List<RuleLog> {
        val sinceTimestamp = LocalDate.now()
            .minusDays(days.toLong())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return logDao.getLogsForRuleSince(ruleId, sinceTimestamp).map { it.toDomain() }
    }

    suspend fun getLogsForCategoryDays(category: Category, days: Int): List<RuleLog> {
        val sinceTimestamp = LocalDate.now()
            .minusDays(days.toLong())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return logDao.getLogsForCategorySince(category, sinceTimestamp).map { it.toDomain() }
    }

    suspend fun getLogsForDay(date: LocalDate): List<RuleLog> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return logDao.getLogsForDay(startOfDay, endOfDay).map { it.toDomain() }
    }

    fun getLogsForDayFlow(date: LocalDate): Flow<List<RuleLog>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return logDao.getLogsForDayFlow(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getLogForRuleOnDay(ruleId: String, date: LocalDate): RuleLog? {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return logDao.getLogForRuleOnDay(ruleId, startOfDay, endOfDay)?.toDomain()
    }

    suspend fun getLogsInRange(startDate: LocalDate, endDate: LocalDate): List<RuleLog> {
        val startTimestamp = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endTimestamp = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return logDao.getLogsInRange(startTimestamp, endTimestamp).map { it.toDomain() }
    }

    suspend fun logCompletion(
        ruleId: String,
        status: LogStatus,
        wasMinimum: Boolean = false,
        skipReason: SkipReason? = null,
        notes: String? = null
    ) {
        val log = RuleLog(
            id = UUID.randomUUID().toString(),
            ruleId = ruleId,
            timestamp = System.currentTimeMillis(),
            status = status,
            skipReason = skipReason,
            wasMinimum = wasMinimum,
            notes = notes
        )
        logDao.insertLog(log.toEntity())
    }

    suspend fun insertLog(log: RuleLog) = logDao.insertLog(log.toEntity())

    suspend fun deleteLog(id: String) = logDao.deleteLog(id)

    suspend fun deleteLogsForRule(ruleId: String) = logDao.deleteLogsForRule(ruleId)

    suspend fun countCompletedSince(sinceTimestamp: Long): Int =
        logDao.countLogsWithStatusSince(LogStatus.COMPLETED, sinceTimestamp)
}
