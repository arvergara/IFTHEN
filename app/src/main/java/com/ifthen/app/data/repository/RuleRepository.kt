package com.ifthen.app.data.repository

import com.ifthen.app.data.local.database.Converters
import com.ifthen.app.data.local.database.RuleDao
import com.ifthen.app.data.local.entities.toDomain
import com.ifthen.app.data.local.entities.toEntity
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.Rule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RuleRepository @Inject constructor(
    private val ruleDao: RuleDao
) {

    fun getAllRules(): Flow<List<Rule>> = ruleDao.getAllRules().map { entities ->
        entities.map { entity ->
            val triggerConfig = Converters.deserializeTriggerConfig(entity.triggerConfigJson)
            entity.toDomain(triggerConfig)
        }
    }

    suspend fun getActiveRules(): List<Rule> = ruleDao.getActiveRules().map { entity ->
        val triggerConfig = Converters.deserializeTriggerConfig(entity.triggerConfigJson)
        entity.toDomain(triggerConfig)
    }

    fun getActiveRulesFlow(): Flow<List<Rule>> = ruleDao.getActiveRulesFlow().map { entities ->
        entities.map { entity ->
            val triggerConfig = Converters.deserializeTriggerConfig(entity.triggerConfigJson)
            entity.toDomain(triggerConfig)
        }
    }

    suspend fun getRuleById(id: String): Rule? = ruleDao.getRuleById(id)?.let { entity ->
        val triggerConfig = Converters.deserializeTriggerConfig(entity.triggerConfigJson)
        entity.toDomain(triggerConfig)
    }

    suspend fun getRulesByCategory(category: Category): List<Rule> =
        ruleDao.getRulesByCategory(category).map { entity ->
            val triggerConfig = Converters.deserializeTriggerConfig(entity.triggerConfigJson)
            entity.toDomain(triggerConfig)
        }

    suspend fun insertRule(rule: Rule) {
        val triggerConfigJson = Converters.serializeTriggerConfig(rule.triggerConfig)
        ruleDao.insertRule(rule.toEntity(triggerConfigJson))
    }

    suspend fun insertRules(rules: List<Rule>) {
        val entities = rules.map { rule ->
            val triggerConfigJson = Converters.serializeTriggerConfig(rule.triggerConfig)
            rule.toEntity(triggerConfigJson)
        }
        ruleDao.insertRules(entities)
    }

    suspend fun updateRule(rule: Rule) {
        val triggerConfigJson = Converters.serializeTriggerConfig(rule.triggerConfig)
        ruleDao.updateRule(rule.toEntity(triggerConfigJson))
    }

    suspend fun deleteRule(rule: Rule) {
        val triggerConfigJson = Converters.serializeTriggerConfig(rule.triggerConfig)
        ruleDao.deleteRule(rule.toEntity(triggerConfigJson))
    }

    suspend fun deleteRuleById(id: String) = ruleDao.deleteRuleById(id)

    suspend fun setRuleActive(id: String, isActive: Boolean) = ruleDao.setRuleActive(id, isActive)

    suspend fun getRulesCount(): Int = ruleDao.getRulesCount()
}
