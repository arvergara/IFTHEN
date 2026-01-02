package com.ifthen.app.domain.model

enum class ModeType {
    RUTINA,
    NO_COLEGIO,
    FERIADO,
    VACACIONES;

    fun getEmoji(): String = when (this) {
        RUTINA -> "\uD83C\uDF92"        // 🎒 Niños en colegio, yo trabajando
        NO_COLEGIO -> "\uD83C\uDFE0"    // 🏠 Niños sin colegio, yo trabajando
        FERIADO -> "\uD83C\uDF89"       // 🎉 Fin de semana o feriado
        VACACIONES -> "\uD83C\uDFD6️"   // 🏖️ Yo de vacaciones
    }

    fun getDisplayName(): String = when (this) {
        RUTINA -> "Rutina"
        NO_COLEGIO -> "No Colegio"
        FERIADO -> "Feriado"
        VACACIONES -> "Vacaciones"
    }
}
