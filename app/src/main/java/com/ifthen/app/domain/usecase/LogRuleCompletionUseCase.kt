package com.ifthen.app.domain.usecase

import com.ifthen.app.data.repository.LogRepository
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.SkipReason
import javax.inject.Inject

class LogRuleCompletionUseCase @Inject constructor(
    private val logRepository: LogRepository
) {
    suspend fun complete(ruleId: String) {
        logRepository.logCompletion(
            ruleId = ruleId,
            status = LogStatus.COMPLETED,
            wasMinimum = false
        )
    }

    suspend fun completeMinimum(ruleId: String) {
        logRepository.logCompletion(
            ruleId = ruleId,
            status = LogStatus.MINIMUM,
            wasMinimum = true
        )
    }

    suspend fun skip(ruleId: String, reason: SkipReason, notes: String? = null) {
        logRepository.logCompletion(
            ruleId = ruleId,
            status = LogStatus.SKIPPED,
            skipReason = reason,
            notes = notes
        )
    }

    suspend fun markMissed(ruleId: String) {
        logRepository.logCompletion(
            ruleId = ruleId,
            status = LogStatus.MISSED
        )
    }
}
