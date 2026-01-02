package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.ModeDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.UserMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class UserModeRepository @Inject constructor(
    private val modeDao: ModeDao
) {

    private fun getDefaultModeForToday(): ModeType {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek

        // Fines de semana siempre son Feriado
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return ModeType.FERIADO
        }

        // Vacaciones de verano: 15 Dic - 28 Feb → No Colegio
        if (isInSummerVacation(today)) {
            return ModeType.NO_COLEGIO
        }

        return ModeType.RUTINA
    }

    private fun isInSummerVacation(date: LocalDate): Boolean {
        val month = date.monthValue
        val day = date.dayOfMonth

        // Diciembre desde el 15
        if (month == 12 && day >= 15) return true
        // Enero completo
        if (month == 1) return true
        // Febrero completo
        if (month == 2) return true

        return false
    }

    suspend fun getCurrentMode(): UserMode {
        val stored = modeDao.getCurrentMode()?.toDomain()
        // Si el modo guardado es de otro día, usar el default
        if (stored != null && isFromToday(stored.timestamp)) {
            return stored
        }
        return UserMode(
            id = UUID.randomUUID().toString(),
            mode = getDefaultModeForToday(),
            timestamp = System.currentTimeMillis()
        )
    }

    fun getCurrentModeFlow(): Flow<UserMode> = modeDao.getCurrentModeFlow().map { entity ->
        val stored = entity?.toDomain()
        if (stored != null && isFromToday(stored.timestamp)) {
            stored
        } else {
            UserMode(
                id = UUID.randomUUID().toString(),
                mode = getDefaultModeForToday(),
                timestamp = System.currentTimeMillis()
            )
        }
    }

    private fun isFromToday(timestamp: Long): Boolean {
        val storedDate = java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        return storedDate == LocalDate.now()
    }

    suspend fun setMode(modeType: ModeType) {
        val mode = UserMode(
            id = UUID.randomUUID().toString(),
            mode = modeType,
            timestamp = System.currentTimeMillis()
        )
        modeDao.insertMode(mode.toEntity())
    }

    suspend fun insertMode(mode: UserMode) = modeDao.insertMode(mode.toEntity())
}
