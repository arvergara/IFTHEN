package com.ifthen.app.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ifthen.app.data.local.entities.RuleEntity
import com.ifthen.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Query("SELECT * FROM rules ORDER BY priority DESC, createdAt DESC")
    fun getAllRules(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules WHERE isActive = 1 ORDER BY priority DESC")
    suspend fun getActiveRules(): List<RuleEntity>

    @Query("SELECT * FROM rules WHERE isActive = 1 ORDER BY priority DESC")
    fun getActiveRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules WHERE id = :id")
    suspend fun getRuleById(id: String): RuleEntity?

    @Query("SELECT * FROM rules WHERE category = :category AND isActive = 1")
    suspend fun getRulesByCategory(category: Category): List<RuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<RuleEntity>)

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Delete
    suspend fun deleteRule(rule: RuleEntity)

    @Query("DELETE FROM rules WHERE id = :id")
    suspend fun deleteRuleById(id: String)

    @Query("UPDATE rules SET isActive = :isActive WHERE id = :id")
    suspend fun setRuleActive(id: String, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM rules")
    suspend fun getRulesCount(): Int
}
