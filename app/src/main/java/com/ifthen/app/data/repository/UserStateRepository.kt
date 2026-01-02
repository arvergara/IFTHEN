package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.StateDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class UserStateRepository @Inject constructor(
    private val stateDao: StateDao
) {

    suspend fun getCurrentState(): UserState {
        // Clean up expired states first
        stateDao.deleteExpiredStates(System.currentTimeMillis())

        return stateDao.getCurrentState()?.toDomain() ?: UserState(
            id = UUID.randomUUID().toString(),
            state = StateType.NORMAL,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getCurrentStateFlow(): Flow<UserState> = stateDao.getCurrentStateFlow().map { entity ->
        entity?.toDomain() ?: UserState(
            id = UUID.randomUUID().toString(),
            state = StateType.NORMAL,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getAllStates(): Flow<List<UserState>> = stateDao.getAllStates().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun setState(stateType: StateType, expiresInHours: Int? = null) {
        val expiresAt = expiresInHours?.let {
            System.currentTimeMillis() + (it * 60 * 60 * 1000L)
        }

        val state = UserState(
            id = UUID.randomUUID().toString(),
            state = stateType,
            timestamp = System.currentTimeMillis(),
            expiresAt = expiresAt
        )
        stateDao.insertState(state.toEntity())
    }

    suspend fun insertState(state: UserState) = stateDao.insertState(state.toEntity())

    suspend fun deleteState(id: String) = stateDao.deleteState(id)
}
