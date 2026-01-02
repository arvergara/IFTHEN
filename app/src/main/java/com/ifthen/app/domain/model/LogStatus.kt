package com.ifthen.app.domain.model

enum class LogStatus {
    COMPLETED,  // Cumplida
    SKIPPED,    // Saltada con razón
    MINIMUM,    // Cumplida en versión mínima
    MISSED      // No respondida
}
