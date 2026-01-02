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
            name = "Meditar",
            category = Category.MENTE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 7,
                minute = 35,
                daysOfWeek = listOf(1, 2, 3, 4, 5)
            ),
            ifCondition = "SI son las 7:35 y es dia de semana",
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
            name = "Ejercitar",
            category = Category.CUERPO,
            triggerType = TriggerType.CALENDAR,
            triggerConfig = TriggerConfig.CalendarTrigger(
                checkTime = "07:45",
                requiresFreeUntil = "08:15",
                daysOfWeek = listOf(2, 3, 4, 5) // M-X-J-V
            ),
            ifCondition = "SI son las 7:45 y tengo libre hasta las 8:15",
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
            name = "Priorizar",
            category = Category.TRABAJO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 8,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            ifCondition = "SI son las 8:30 y empiezo el dia",
            action = "Definir las 3 prioridades: Que DEBE hacerse hoy?",
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
            ifCondition = "SI son las 20:00 y aun no he bebido",
            action = "Confirmar 0 copas hoy. Max 3/semana, max 2/dia.",
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
            ifCondition = "SI son las 19:00 y tengo energia",
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
            ifCondition = "SI son las 19:30 y los ninos estan en casa",
            action = "Telefono fuera del espacio comun hasta que se acuesten",
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
            ifCondition = "SI son las 19:30 y estoy con la familia",
            action = "10 min de caminata o conversa de calidad",
            durationMinutes = 10,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = ModeType.entries.toList()
        ),

        // === 8. CUERPO - Fasting (20:30) ===
        Rule(
            id = "rule_cuerpo_fasting",
            name = "Fasting 11am",
            category = Category.CUERPO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 20,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            ifCondition = "SI son las 20:30 y termine de comer",
            action = "Comenzar ayuno. Solo agua/te hasta manana 11:00",
            durationMinutes = 5,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        ),

        // === 9. TRABAJO - Revisar cumplimiento (21:00) ===
        Rule(
            id = "rule_trabajo_cumplimiento",
            name = "Rev. Prioridades",
            category = Category.TRABAJO,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 21,
                minute = 0,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
            ),
            ifCondition = "SI son las 21:00 y defini prioridades hoy",
            action = "Revisar: Se cumplieron o delegaron las 3 prioridades?",
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
            ifCondition = "SI son las 21:30 y voy a relajarme",
            action = "10 min de estiramientos",
            durationMinutes = 10,
            priority = Priority.MEDIA,
            minimumAction = "5 min estiramientos basicos",
            minimumDurationMinutes = 5,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO, ModeType.FERIADO)
        ),

        // === 11. MENTE - Pantallas off (22:45) ===
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
            ifCondition = "SI son las 22:45 y manana hay que madrugar",
            action = "Apagar pantallas. Leer hasta las 23:30",
            durationMinutes = 45,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null,
            applicableModes = listOf(ModeType.RUTINA, ModeType.NO_COLEGIO)
        )
    )
}
