package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifthen.app.data.local.entities.LogEntity
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.LogStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE ruleId = :ruleId ORDER BY timestamp DESC")
    fun getLogsForRule(ruleId: String): Flow<List<LogEntity>>

    @Query("""
        SELECT * FROM logs
        WHERE ruleId = :ruleId
        AND timestamp >= :sinceTimestamp
        ORDER BY timestamp DESC
    """)
    suspend fun getLogsForRuleSince(ruleId: String, sinceTimestamp: Long): List<LogEntity>

    @Query("""
        SELECT l.* FROM logs l
        INNER JOIN rules r ON l.ruleId = r.id
        WHERE r.category = :category
        AND l.timestamp >= :sinceTimestamp
        ORDER BY l.timestamp DESC
    """)
    suspend fun getLogsForCategorySince(category: Category, sinceTimestamp: Long): List<LogEntity>

    @Query("""
        SELECT * FROM logs
        WHERE timestamp >= :startOfDay
        AND timestamp < :endOfDay
        ORDER BY timestamp DESC
    """)
    suspend fun getLogsForDay(startOfDay: Long, endOfDay: Long): List<LogEntity>

    @Query("""
        SELECT * FROM logs
        WHERE timestamp >= :startOfDay
        AND timestamp < :endOfDay
        ORDER BY timestamp DESC
    """)
    fun getLogsForDayFlow(startOfDay: Long, endOfDay: Long): Flow<List<LogEntity>>

    @Query("""
        SELECT * FROM logs
        WHERE ruleId = :ruleId
        AND timestamp >= :startOfDay
        AND timestamp < :endOfDay
        LIMIT 1
    """)
    suspend fun getLogForRuleOnDay(ruleId: String, startOfDay: Long, endOfDay: Long): LogEntity?

    @Query("""
        SELECT * FROM logs
        WHERE timestamp >= :startTimestamp
        AND timestamp < :endTimestamp
        ORDER BY timestamp DESC
    """)
    suspend fun getLogsInRange(startTimestamp: Long, endTimestamp: Long): List<LogEntity>

    @Query("""
        SELECT COUNT(*) FROM logs
        WHERE status = :status
        AND timestamp >= :sinceTimestamp
    """)
    suspend fun countLogsWithStatusSince(status: LogStatus, sinceTimestamp: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity)

    @Query("DELETE FROM logs WHERE id = :id")
    suspend fun deleteLog(id: String)

    @Query("DELETE FROM logs WHERE ruleId = :ruleId")
    suspend fun deleteLogsForRule(ruleId: String)
}
