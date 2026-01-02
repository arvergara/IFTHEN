package com.ifthen.app.domain.model

enum class Category {
    FAMILIA,
    CUERPO,
    MENTE,
    APRENDIZAJE,
    TRABAJO;

    fun getEmoji(): String = when (this) {
        FAMILIA -> "\uD83C\uDFE0"
        CUERPO -> "\uD83C\uDFC3"
        MENTE -> "\uD83E\uDDE0"
        APRENDIZAJE -> "\uD83D\uDCDA"
        TRABAJO -> "\uD83D\uDCBC"
    }

    fun getDisplayName(): String = when (this) {
        FAMILIA -> "Familia"
        CUERPO -> "Cuerpo"
        MENTE -> "Mente"
        APRENDIZAJE -> "Aprendizaje"
        TRABAJO -> "Trabajo"
    }
}
