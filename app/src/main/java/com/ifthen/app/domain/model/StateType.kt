package com.ifthen.app.domain.model

enum class StateType {
    NORMAL,
    DIA_PESADO,
    CANSADO,
    ACELERADO,
    PROCRASTINANDO;

    fun getDisplayName(): String = when (this) {
        NORMAL -> "Normal"
        DIA_PESADO -> "Día pesado"
        CANSADO -> "Cansado"
        ACELERADO -> "Acelerado"
        PROCRASTINANDO -> "Procrastinando"
    }
}
