package com.ifthen.app.domain.usecase

import com.ifthen.app.domain.engine.RuleEngine
import com.ifthen.app.domain.model.TriggeredRule
import javax.inject.Inject

class EvaluateRulesUseCase @Inject constructor(
    private val ruleEngine: RuleEngine
) {
    suspend operator fun invoke(): List<TriggeredRule> = ruleEngine.evaluateRules()

    suspend fun forEvent(eventName: String): List<TriggeredRule> =
        ruleEngine.evaluateEventTriggers(eventName)
}
