package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ifthen.app.data.local.entities.DailyPriorityEntity
import com.ifthen.app.domain.model.PriorityStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPriorityDao {

    @Query("SELECT * FROM daily_priorities WHERE dateEpochDay = :epochDay ORDER BY `order` ASC")
    fun getPrioritiesForDayFlow(epochDay: Long): Flow<List<DailyPriorityEntity>>

    @Query("SELECT * FROM daily_priorities WHERE dateEpochDay = :epochDay ORDER BY `order` ASC")
    suspend fun getPrioritiesForDay(epochDay: Long): List<DailyPriorityEntity>

    @Query("SELECT * FROM daily_priorities WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay, `order` ASC")
    suspend fun getPrioritiesInRange(startEpochDay: Long, endEpochDay: Long): List<DailyPriorityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriority(priority: DailyPriorityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriorities(priorities: List<DailyPriorityEntity>)

    @Update
    suspend fun updatePriority(priority: DailyPriorityEntity)

    @Query("UPDATE daily_priorities SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: PriorityStatus)

    @Query("DELETE FROM daily_priorities WHERE dateEpochDay = :epochDay")
    suspend fun deletePrioritiesForDay(epochDay: Long)

    @Query("DELETE FROM daily_priorities WHERE id = :id")
    suspend fun deletePriority(id: String)
}
