package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.DailyPriorityDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.DailyPriority
import com.ifthen.app.domain.model.PriorityStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class DailyPriorityRepository @Inject constructor(
    private val dailyPriorityDao: DailyPriorityDao
) {

    fun getPrioritiesForDayFlow(date: LocalDate): Flow<List<DailyPriority>> {
        return dailyPriorityDao.getPrioritiesForDayFlow(date.toEpochDay()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getPrioritiesForDay(date: LocalDate): List<DailyPriority> {
        return dailyPriorityDao.getPrioritiesForDay(date.toEpochDay()).map { it.toDomain() }
    }

    suspend fun getPrioritiesInRange(startDate: LocalDate, endDate: LocalDate): List<DailyPriority> {
        return dailyPriorityDao.getPrioritiesInRange(
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { it.toDomain() }
    }

    suspend fun savePriorities(date: LocalDate, priorities: List<String>) {
        // Delete existing priorities for the day
        dailyPriorityDao.deletePrioritiesForDay(date.toEpochDay())

        // Insert new priorities
        val entities = priorities.mapIndexed { index, text ->
            DailyPriority(
                date = date,
                order = index + 1,
                text = text,
                status = PriorityStatus.PENDING
            ).toEntity()
        }
        dailyPriorityDao.insertPriorities(entities)
    }

    suspend fun updateStatus(priorityId: String, status: PriorityStatus) {
        dailyPriorityDao.updateStatus(priorityId, status)
    }

    suspend fun hasPrioritiesForToday(): Boolean {
        return dailyPriorityDao.getPrioritiesForDay(LocalDate.now().toEpochDay()).isNotEmpty()
    }
}
