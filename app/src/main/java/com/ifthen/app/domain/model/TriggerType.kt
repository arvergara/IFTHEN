package com.ifthen.app.domain.model

enum class TriggerType {
    TIME,       // Hora específica
    CALENDAR,   // Basado en calendario (ventana libre, sin reuniones)
    PATTERN,    // Patrón detectado (2 días sin X)
    MANUAL,     // Declaración del usuario
    EVENT       // Evento declarado (terminé desayuno)
}
