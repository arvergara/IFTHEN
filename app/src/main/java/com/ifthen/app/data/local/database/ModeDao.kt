package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifthen.app.data.local.entities.UserModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModeDao {

    @Query("SELECT * FROM user_modes ORDER BY timestamp DESC LIMIT 1")
    suspend fun getCurrentMode(): UserModeEntity?

    @Query("SELECT * FROM user_modes ORDER BY timestamp DESC LIMIT 1")
    fun getCurrentModeFlow(): Flow<UserModeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMode(mode: UserModeEntity)

    @Query("DELETE FROM user_modes")
    suspend fun deleteAllModes()
}
