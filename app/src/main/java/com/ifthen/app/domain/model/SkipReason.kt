package com.ifthen.app.domain.model

enum class SkipReason {
    REUNION_TEMPRANO,
    DIA_PESADO,
    YA_LO_HICE,
    OTRO;

    fun getDisplayName(): String = when (this) {
        REUNION_TEMPRANO -> "Reunión temprano"
        DIA_PESADO -> "Día pesado"
        YA_LO_HICE -> "Ya lo hice"
        OTRO -> "Otro"
    }
}
