package com.ifthen.app.data

import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.Priority
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType

object DefaultRules {

    fun getDefaultRules(): List<Rule> = listOf(

        // === 1. MENTE - Meditacion (7:35) ===
        Rule(
            id = "rule_mente_meditacion",
            name = "Meditacion manana",
            category = Category.MENTE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 7,
                minute = 35,
                daysOfWeek = listOf(1, 2, 3, 4, 5)
            ),
            action = "7 minutos de silencio/meditacion con cronometro",
            durationMinutes = 7,
            priority = Priority.ALTA,
            minimumAction = "3 minutos de respiracion consciente",
            minimumDurationMinutes = 3,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 2. CUERPO - Ejercicio (7:45) ===
        Rule(
            id = "rule_cuerpo_ejercicio",
            name = "Ejercicio manana",
            category = Category.CUERPO,
            triggerType = TriggerType.CALENDAR,
            triggerConfig = TriggerConfig.CalendarTrigger(
                checkTime = "07:45",
                requiresFreeUntil = "08:15",
                daysOfWeek = listOf(2, 3, 4, 5) // M-X-J-V
            ),
            action = "30 min de movimiento (caminar, bici o fuerza)",
            durationMinutes = 30,
            priority = Priority.ALTA,
            minimumAction = "15 min de caminata",
            minimumDurationMinutes = 15,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 3. TRABAJO - Prioridades (8:30) ===
        Rule(
            id = "rule_trabajo_prioridades",
            name = "Revisar prioridades",
            category = Category.TRABAJO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 8,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "Cuales son las 3 prioridades del dia? Que DEBE hacerse hoy?",
            durationMinutes = 15,
            priority = Priority.ALTA,
            minimumAction = "Identificar 1 tarea critica",
            minimumDurationMinutes = 5,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 4. CUERPO - Alcohol (Diario 20:00) ===
        Rule(
            id = "rule_cuerpo_alcohol",
            name = "0 copas hoy",
            category = Category.CUERPO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 20,
                minute = 0,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "No tomaste alcohol hoy? Max 3 copas/semana, max 2/dia.",
            durationMinutes = 1,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = ModeType.entries.toList()
        ),

        // === 5. APRENDIZAJE - Sprint IA (19:00) ===
        Rule(
            id = "rule_aprendizaje_sprint",
            name = "Sprint IA",
            category = Category.APRENDIZAJE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 19,
                minute = 0,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "30 min Sprint IA: leer, probar, reflexionar",
            durationMinutes = 30,
            priority = Priority.MEDIA,
            minimumAction = "15 min de lectura enfocada",
            minimumDurationMinutes = 15,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 6. FAMILIA - Telefono fuera (19:30) ===
        Rule(
            id = "rule_familia_telefono",
            name = "Telefono fuera",
            category = Category.FAMILIA,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 19,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "Telefono fuera del espacio comun hasta que los ninos se acuesten",
            durationMinutes = 120,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = ModeType.entries.toList()
        ),

        // === 7. FAMILIA - Tiempo de Calidad (19:30) ===
        Rule(
            id = "rule_familia_calidad",
            name = "Tiempo de Calidad",
            category = Category.FAMILIA,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 19,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "10 min de caminata o conversa de calidad con tu senora o hijos",
            durationMinutes = 10,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = ModeType.entries.toList()
        ),

        // === 8. CUERPO - Fasting (20:30) ===
        Rule(
            id = "rule_cuerpo_fasting",
            name = "Fasting hasta 11am",
            category = Category.CUERPO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 20,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "Comienza ayuno. Solo agua o te hasta manana 11:00 am",
            durationMinutes = 5,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 9. TRABAJO - Revisar cumplimiento (21:00) ===
        Rule(
            id = "rule_trabajo_cumplimiento",
            name = "Revisar cumplimiento",
            category = Category.TRABAJO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 21,
                minute = 0,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "Revisa las 3 prioridades del dia. Se cumplieron o delegaron?",
            durationMinutes = 5,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 10. CUERPO - Estiramiento (21:30) ===
        Rule(
            id = "rule_cuerpo_estiramiento",
            name = "Estiramiento Nocturno",
            category = Category.CUERPO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 21,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            action = "10 min de estiramientos antes de relajarte",
            durationMinutes = 10,
            priority = Priority.MEDIA,
            minimumAction = "5 min estiramientos basicos",
            minimumDurationMinutes = 5,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO, ModeType.FERIADO)
        ),

        // === 10. MENTE - Pantallas off (22:45) ===
        Rule(
            id = "rule_mente_pantallas_off",
            name = "Pantallas off",
            category = Category.MENTE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 22,
                minute = 45,
                daysOfWeek = listOf(7, 1, 2, 3, 4) // Dom-Jue
            ),
            action = "Apagar pantallas. Hora de leer para dormir 23:30",
            durationMinutes = 45,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        )
    )
}
