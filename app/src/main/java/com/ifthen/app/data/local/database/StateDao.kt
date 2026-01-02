package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifthen.app.data.local.entities.UserStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StateDao {

    @Query("SELECT * FROM user_states ORDER BY timestamp DESC LIMIT 1")
    suspend fun getCurrentState(): UserStateEntity?

    @Query("SELECT * FROM user_states ORDER BY timestamp DESC LIMIT 1")
    fun getCurrentStateFlow(): Flow<UserStateEntity?>

    @Query("SELECT * FROM user_states ORDER BY timestamp DESC")
    fun getAllStates(): Flow<List<UserStateEntity>>

    @Query("""
        SELECT * FROM user_states
        WHERE timestamp >= :sinceTimestamp
        ORDER BY timestamp DESC
    """)
    suspend fun getStatesSince(sinceTimestamp: Long): List<UserStateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: UserStateEntity)

    @Query("DELETE FROM user_states WHERE id = :id")
    suspend fun deleteState(id: String)

    @Query("""
        DELETE FROM user_states
        WHERE expiresAt IS NOT NULL
        AND expiresAt < :currentTime
    """)
    suspend fun deleteExpiredStates(currentTime: Long)
}
