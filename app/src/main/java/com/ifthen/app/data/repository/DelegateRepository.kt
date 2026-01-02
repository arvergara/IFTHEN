package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.DelegateDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.Delegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DelegateRepository @Inject constructor(
    private val delegateDao: DelegateDao
) {

    fun getAllDelegatesFlow(): Flow<List<Delegate>> = delegateDao.getAllDelegatesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getAllDelegates(): List<Delegate> = delegateDao.getAllDelegates().map { it.toDomain() }

    suspend fun getDelegateById(id: String): Delegate? = delegateDao.getDelegateById(id)?.toDomain()

    suspend fun insertDelegate(delegate: Delegate) {
        delegateDao.insertDelegate(delegate.toEntity())
    }

    suspend fun updateDelegate(delegate: Delegate) {
        delegateDao.updateDelegate(delegate.toEntity())
    }

    suspend fun deleteDelegate(delegate: Delegate) {
        delegateDao.deleteDelegate(delegate.toEntity())
    }

    suspend fun deleteDelegateById(id: String) {
        delegateDao.deleteDelegateById(id)
    }
}
