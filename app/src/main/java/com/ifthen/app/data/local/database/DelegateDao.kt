package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ifthen.app.data.local.entities.DelegateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DelegateDao {

    @Query("SELECT * FROM delegates ORDER BY name ASC")
    fun getAllDelegatesFlow(): Flow<List<DelegateEntity>>

    @Query("SELECT * FROM delegates ORDER BY name ASC")
    suspend fun getAllDelegates(): List<DelegateEntity>

    @Query("SELECT * FROM delegates WHERE id = :id")
    suspend fun getDelegateById(id: String): DelegateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelegate(delegate: DelegateEntity)

    @Update
    suspend fun updateDelegate(delegate: DelegateEntity)

    @Delete
    suspend fun deleteDelegate(delegate: DelegateEntity)

    @Query("DELETE FROM delegates WHERE id = :id")
    suspend fun deleteDelegateById(id: String)
}
