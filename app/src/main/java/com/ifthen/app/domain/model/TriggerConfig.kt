package com.ifthen.app.domain.model

sealed class TriggerConfig {
    data class TimeTrigger(
        val hour: Int,
        val minute: Int,
        val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7) // 1=Lunes
    ) : TriggerConfig()

    data class CalendarTrigger(
        val checkTime: String,           // "07:30"
        val requiresFreeUntil: String,   // "08:30"
        val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)
    ) : TriggerConfig()

    data class PatternTrigger(
        val relatedRuleId: String?,      // Regla a monitorear
        val daysWithoutCompletion: Int,  // Días sin cumplir
        val category: Category?          // O categoría completa
    ) : TriggerConfig()

    data class ManualTrigger(
        val stateRequired: StateType?    // Estado que activa la regla
    ) : TriggerConfig()

    data class EventTrigger(
        val eventName: String            // "desayuno_terminado", "computador_apagado"
    ) : TriggerConfig()
}
