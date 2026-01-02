# IF-THEN Personal System — Especificación Completa para Claude Code

## 🎯 Objetivo del Proyecto

Crear una aplicación Android nativa que automatice decisiones personales mediante reglas IF-THEN claras, medibles y sostenibles. La app debe reducir fricción, no añadirla. No es una app de motivación, es arquitectura de decisiones.

---

## 📱 Plataforma y Stack Técnico

| Componente | Tecnología |
|------------|------------|
| Plataforma | Android nativo (min SDK 26 / Android 8.0) |
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Base de datos | Room (local) |
| Scheduling | AlarmManager + WorkManager |
| Calendario | CalendarContract API (Google Calendar) |
| Arquitectura | MVVM + Clean Architecture |
| DI | Hilt |

---

## 🏗️ Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/ifthen/app/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── database/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── RuleDao.kt
│   │   │   │   │   ├── LogDao.kt
│   │   │   │   │   └── StateDao.kt
│   │   │   │   └── entities/
│   │   │   │       ├── RuleEntity.kt
│   │   │   │       ├── LogEntity.kt
│   │   │   │       └── UserStateEntity.kt
│   │   │   ├── repository/
│   │   │   │   ├── RuleRepository.kt
│   │   │   │   ├── LogRepository.kt
│   │   │   │   └── CalendarRepository.kt
│   │   │   └── calendar/
│   │   │       └── CalendarProvider.kt
│   │   │
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Rule.kt
│   │   │   │   ├── RuleLog.kt
│   │   │   │   ├── UserState.kt
│   │   │   │   ├── TriggerType.kt
│   │   │   │   └── Category.kt
│   │   │   ├── usecase/
│   │   │   │   ├── EvaluateRulesUseCase.kt
│   │   │   │   ├── LogRuleCompletionUseCase.kt
│   │   │   │   ├── DetectPatternsUseCase.kt
│   │   │   │   └── CheckCalendarUseCase.kt
│   │   │   └── engine/
│   │   │       └── RuleEngine.kt
│   │   │
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── Theme.kt
│   │   │   │   ├── Color.kt
│   │   │   │   └── Type.kt
│   │   │   ├── screens/
│   │   │   │   ├── today/
│   │   │   │   │   ├── TodayScreen.kt
│   │   │   │   │   └── TodayViewModel.kt
│   │   │   │   ├── rules/
│   │   │   │   │   ├── RulesScreen.kt
│   │   │   │   │   ├── RulesViewModel.kt
│   │   │   │   │   └── RuleEditorScreen.kt
│   │   │   │   └── week/
│   │   │   │       ├── WeekScreen.kt
│   │   │   │       └── WeekViewModel.kt
│   │   │   ├── components/
│   │   │   │   ├── RuleCard.kt
│   │   │   │   ├── StateSelector.kt
│   │   │   │   ├── ProgressBar.kt
│   │   │   │   └── PatternSuggestion.kt
│   │   │   └── navigation/
│   │   │       └── AppNavigation.kt
│   │   │
│   │   ├── notification/
│   │   │   ├── NotificationManager.kt
│   │   │   ├── NotificationActionReceiver.kt
│   │   │   └── RuleNotificationBuilder.kt
│   │   │
│   │   ├── scheduler/
│   │   │   ├── RuleScheduler.kt
│   │   │   ├── AlarmReceiver.kt
│   │   │   └── RuleWorker.kt
│   │   │
│   │   ├── di/
│   │   │   ├── AppModule.kt
│   │   │   ├── DatabaseModule.kt
│   │   │   └── RepositoryModule.kt
│   │   │
│   │   └── MainActivity.kt
│   │
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   └── drawable/
│   │       └── (iconos categorías)
│   │
│   └── AndroidManifest.xml
│
├── build.gradle.kts (app)
└── build.gradle.kts (project)
```

---

## 📊 Modelos de Datos

### Rule (Regla IF-THEN)

```kotlin
data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,                    // Nombre corto
    val category: Category,              // FAMILIA, CUERPO, MENTE, APRENDIZAJE
    val triggerType: TriggerType,        // TIME, CALENDAR, PATTERN, MANUAL
    val triggerConfig: TriggerConfig,    // Configuración específica del gatillo
    val action: String,                  // Descripción de la acción
    val durationMinutes: Int,            // Duración estimada
    val priority: Priority,              // ALTA, MEDIA
    val minimumAction: String?,          // Versión reducida para días difíciles
    val minimumDurationMinutes: Int?,    // Duración del mínimo
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Category {
    FAMILIA, CUERPO, MENTE, APRENDIZAJE
}

enum class Priority {
    ALTA, MEDIA
}

enum class TriggerType {
    TIME,           // Hora específica
    CALENDAR,       // Basado en calendario (ventana libre, sin reuniones)
    PATTERN,        // Patrón detectado (2 días sin X)
    MANUAL,         // Declaración del usuario
    EVENT           // Evento declarado (terminé desayuno)
}

sealed class TriggerConfig {
    data class TimeTrigger(
        val hour: Int,
        val minute: Int,
        val daysOfWeek: List<Int> = listOf(1,2,3,4,5,6,7) // 1=Lunes
    ) : TriggerConfig()
    
    data class CalendarTrigger(
        val checkTime: String,           // "07:30"
        val requiresFreeUntil: String,   // "08:30"
        val daysOfWeek: List<Int> = listOf(1,2,3,4,5,6,7)
    ) : TriggerConfig()
    
    data class PatternTrigger(
        val relatedRuleId: String?,      // Regla a monitorear
        val daysWithoutCompletion: Int,  // Días sin cumplir
        val category: Category?          // O categoría completa
    ) : TriggerConfig()
    
    data class ManualTrigger(
        val stateRequired: UserState?    // Estado que activa la regla
    ) : TriggerConfig()
    
    data class EventTrigger(
        val eventName: String            // "desayuno_terminado", "computador_apagado"
    ) : TriggerConfig()
}
```

### RuleLog (Registro de cumplimiento)

```kotlin
data class RuleLog(
    val id: String = UUID.randomUUID().toString(),
    val ruleId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: LogStatus,
    val skipReason: SkipReason? = null,
    val wasMinimum: Boolean = false,     // Si aplicó versión mínima
    val notes: String? = null
)

enum class LogStatus {
    COMPLETED,      // Cumplida
    SKIPPED,        // Saltada con razón
    MINIMUM,        // Cumplida en versión mínima
    MISSED          // No respondida
}

enum class SkipReason {
    REUNION_TEMPRANO,
    DIA_PESADO,
    YA_LO_HICE,
    OTRO
}
```

### UserState (Estado del usuario)

```kotlin
data class UserState(
    val id: String = UUID.randomUUID().toString(),
    val state: StateType,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null          // Cuándo expira este estado
)

enum class StateType {
    NORMAL,
    DIA_PESADO,
    CANSADO,
    ACELERADO,
    IRRITADO
}
```

---

## ⚙️ Motor de Reglas (RuleEngine)

```kotlin
class RuleEngine @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val logRepository: LogRepository,
    private val calendarRepository: CalendarRepository,
    private val userStateRepository: UserStateRepository
) {
    
    /**
     * Evalúa todas las reglas activas y determina cuáles deben dispararse ahora
     */
    suspend fun evaluateRules(): List<TriggeredRule> {
        val activeRules = ruleRepository.getActiveRules()
        val currentState = userStateRepository.getCurrentState()
        val triggeredRules = mutableListOf<TriggeredRule>()
        
        for (rule in activeRules) {
            val shouldTrigger = when (rule.triggerType) {
                TriggerType.TIME -> evaluateTimeTrigger(rule)
                TriggerType.CALENDAR -> evaluateCalendarTrigger(rule)
                TriggerType.PATTERN -> evaluatePatternTrigger(rule)
                TriggerType.MANUAL -> evaluateManualTrigger(rule, currentState)
                TriggerType.EVENT -> false // Se activa externamente
            }
            
            if (shouldTrigger) {
                val useMinimum = currentState.state in listOf(
                    StateType.DIA_PESADO, 
                    StateType.CANSADO
                )
                triggeredRules.add(TriggeredRule(rule, useMinimum))
            }
        }
        
        return triggeredRules.sortedByDescending { it.rule.priority }
    }
    
    private suspend fun evaluateTimeTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.TimeTrigger ?: return false
        val now = LocalDateTime.now()
        return now.hour == config.hour && 
               now.minute == config.minute &&
               now.dayOfWeek.value in config.daysOfWeek
    }
    
    private suspend fun evaluateCalendarTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.CalendarTrigger ?: return false
        val now = LocalDateTime.now()
        val checkTime = LocalTime.parse(config.checkTime)
        
        if (now.toLocalTime() != checkTime) return false
        if (now.dayOfWeek.value !in config.daysOfWeek) return false
        
        // Verificar que no hay reuniones hasta la hora indicada
        val freeUntil = LocalTime.parse(config.requiresFreeUntil)
        return calendarRepository.isFreeUntil(
            from = now,
            until = now.toLocalDate().atTime(freeUntil)
        )
    }
    
    private suspend fun evaluatePatternTrigger(rule: Rule): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.PatternTrigger ?: return false
        
        val daysToCheck = config.daysWithoutCompletion
        val logs = if (config.relatedRuleId != null) {
            logRepository.getLogsForRule(config.relatedRuleId, daysToCheck)
        } else if (config.category != null) {
            logRepository.getLogsForCategory(config.category, daysToCheck)
        } else {
            return false
        }
        
        // Si no hay logs completados en los últimos N días, disparar
        val completedLogs = logs.filter { it.status == LogStatus.COMPLETED }
        return completedLogs.isEmpty()
    }
    
    private fun evaluateManualTrigger(rule: Rule, currentState: UserState): Boolean {
        val config = rule.triggerConfig as? TriggerConfig.ManualTrigger ?: return false
        return config.stateRequired == null || currentState.state == config.stateRequired?.state
    }
}

data class TriggeredRule(
    val rule: Rule,
    val useMinimum: Boolean
)
```

---

## 🔔 Sistema de Notificaciones

### Estructura de Notificación

```kotlin
class RuleNotificationBuilder @Inject constructor(
    private val context: Context
) {
    
    fun buildRuleNotification(triggeredRule: TriggeredRule): Notification {
        val rule = triggeredRule.rule
        val useMinimum = triggeredRule.useMinimum
        
        val action = if (useMinimum && rule.minimumAction != null) {
            rule.minimumAction
        } else {
            rule.action
        }
        
        val duration = if (useMinimum && rule.minimumDurationMinutes != null) {
            rule.minimumDurationMinutes
        } else {
            rule.durationMinutes
        }
        
        val title = when (rule.category) {
            Category.FAMILIA -> "🏠 ${rule.name}"
            Category.CUERPO -> "🏃 ${rule.name}"
            Category.MENTE -> "🧠 ${rule.name}"
            Category.APRENDIZAJE -> "📚 ${rule.name}"
        }
        
        val text = "$action ($duration min)"
        
        // Acciones de la notificación
        val doneIntent = createActionIntent(rule.id, NotificationAction.DONE)
        val skipIntent = createActionIntent(rule.id, NotificationAction.SKIP)
        val minimumIntent = createActionIntent(rule.id, NotificationAction.MINIMUM)
        
        return NotificationCompat.Builder(context, CHANNEL_RULES)
            .setSmallIcon(R.drawable.ic_rule)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_check, "HECHO ✓", doneIntent)
            .addAction(R.drawable.ic_skip, "SALTAR", skipIntent)
            .apply {
                if (!useMinimum && rule.minimumAction != null) {
                    addAction(
                        R.drawable.ic_minimum, 
                        "MÍNIMO ${rule.minimumDurationMinutes} MIN", 
                        minimumIntent
                    )
                }
            }
            .build()
    }
}

enum class NotificationAction {
    DONE, SKIP, MINIMUM
}
```

### Receiver para Acciones

```kotlin
class NotificationActionReceiver : BroadcastReceiver() {
    
    @Inject lateinit var logRepository: LogRepository
    
    override fun onReceive(context: Context, intent: Intent) {
        val ruleId = intent.getStringExtra("rule_id") ?: return
        val action = intent.getSerializableExtra("action") as? NotificationAction ?: return
        
        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                NotificationAction.DONE -> {
                    logRepository.logCompletion(ruleId, LogStatus.COMPLETED)
                    cancelNotification(context, ruleId)
                }
                NotificationAction.SKIP -> {
                    // Mostrar dialog para seleccionar razón
                    showSkipReasonDialog(context, ruleId)
                }
                NotificationAction.MINIMUM -> {
                    logRepository.logCompletion(ruleId, LogStatus.MINIMUM, wasMinimum = true)
                    cancelNotification(context, ruleId)
                }
            }
        }
    }
}
```

---

## 📱 Pantallas UI (Jetpack Compose)

### TodayScreen (Pantalla Principal)

```kotlin
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.dateFormatted,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Selector de estado
            StateSelector(
                currentState = uiState.currentState,
                onStateChange = { viewModel.updateState(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de reglas del día
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.todayRules) { ruleWithStatus ->
                    RuleCard(
                        rule = ruleWithStatus.rule,
                        status = ruleWithStatus.status,
                        onComplete = { viewModel.markComplete(it) },
                        onSkip = { viewModel.markSkipped(it) },
                        onMinimum = { viewModel.markMinimum(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun StateSelector(
    currentState: StateType,
    onStateChange: (StateType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estado:",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Dropdown con estados
            ExposedDropdownMenuBox(...) {
                // NORMAL, DIA_PESADO, CANSADO, etc.
            }
        }
    }
}

@Composable
fun RuleCard(
    rule: Rule,
    status: LogStatus?,
    onComplete: (String) -> Unit,
    onSkip: (String) -> Unit,
    onMinimum: (String) -> Unit
) {
    val categoryIcon = when (rule.category) {
        Category.FAMILIA -> "🏠"
        Category.CUERPO -> "🏃"
        Category.MENTE -> "🧠"
        Category.APRENDIZAJE -> "📚"
    }
    
    val statusIcon = when (status) {
        LogStatus.COMPLETED -> "✓"
        LogStatus.MINIMUM -> "½"
        LogStatus.SKIPPED -> "–"
        else -> "·"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = categoryIcon, style = MaterialTheme.typography.titleLarge)
                
                Column {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${rule.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (status == null) {
                // Botones de acción
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onComplete(rule.id) }) {
                        Icon(Icons.Default.Check, "Completar")
                    }
                    IconButton(onClick = { onSkip(rule.id) }) {
                        Icon(Icons.Default.Close, "Saltar")
                    }
                }
            } else {
                Text(
                    text = statusIcon,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
```

### WeekScreen (Vista Semanal)

```kotlin
@Composable
fun WeekScreen(
    viewModel: WeekViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Esta semana") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Porcentaje de cumplimiento
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Cumplimiento: ${uiState.completionPercentage}%",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = uiState.completionPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Día con menor cumplimiento
            if (uiState.worstDay != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📉")
                        Text(
                            text = "${uiState.worstDay.name} baja (${uiState.worstDay.percentage}%)",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sugerencias de patrones
            uiState.suggestions.forEach { suggestion ->
                PatternSuggestionCard(
                    suggestion = suggestion,
                    onAccept = { viewModel.applySuggestion(it) },
                    onDismiss = { viewModel.dismissSuggestion(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PatternSuggestionCard(
    suggestion: PatternSuggestion,
    onAccept: (PatternSuggestion) -> Unit,
    onDismiss: (PatternSuggestion) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text("💡")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = suggestion.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { onAccept(suggestion) }) {
                    Text("AJUSTAR")
                }
                TextButton(onClick = { onDismiss(suggestion) }) {
                    Text("IGNORAR")
                }
            }
        }
    }
}
```

---

## 📋 Reglas Pre-cargadas (Seed Data)

```kotlin
object DefaultRules {
    
    fun getDefaultRules(): List<Rule> = listOf(
        
        // === FAMILIA ===
        Rule(
            name = "Teléfono fuera",
            category = Category.FAMILIA,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(hour = 19, minute = 30),
            action = "Teléfono fuera del espacio común hasta que los niños se acuesten",
            durationMinutes = 120,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null
        ),
        
        Rule(
            name = "Atención plena hijos",
            category = Category.FAMILIA,
            triggerType = TriggerType.MANUAL,
            triggerConfig = TriggerConfig.ManualTrigger(stateRequired = null),
            action = "Contacto visual + respuesta completa antes de cualquier otra acción",
            durationMinutes = 5,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null
        ),
        
        // === CUERPO ===
        Rule(
            name = "Movimiento mañana",
            category = Category.CUERPO,
            triggerType = TriggerType.CALENDAR,
            triggerConfig = TriggerConfig.CalendarTrigger(
                checkTime = "07:30",
                requiresFreeUntil = "08:30",
                daysOfWeek = listOf(1, 2, 3, 4, 5) // Lunes a Viernes
            ),
            action = "25-30 min de movimiento (caminar, bici o fuerza simple)",
            durationMinutes = 25,
            priority = Priority.ALTA,
            minimumAction = "10 min de caminata o estiramientos",
            minimumDurationMinutes = 10
        ),
        
        Rule(
            name = "Reset ejercicio",
            category = Category.CUERPO,
            triggerType = TriggerType.PATTERN,
            triggerConfig = TriggerConfig.PatternTrigger(
                relatedRuleId = null,
                daysWithoutCompletion = 2,
                category = Category.CUERPO
            ),
            action = "Caminata 30 min + fuerza 10 min",
            durationMinutes = 40,
            priority = Priority.ALTA,
            minimumAction = "Caminata 15 min",
            minimumDurationMinutes = 15
        ),
        
        // === MENTE ===
        Rule(
            name = "Meditación post-desayuno",
            category = Category.MENTE,
            triggerType = TriggerType.EVENT,
            triggerConfig = TriggerConfig.EventTrigger(eventName = "desayuno_terminado"),
            action = "7 minutos de silencio/meditación con cronómetro",
            durationMinutes = 7,
            priority = Priority.ALTA,
            minimumAction = "3 minutos de respiración consciente",
            minimumDurationMinutes = 3
        ),
        
        Rule(
            name = "Pausa regulación",
            category = Category.MENTE,
            triggerType = TriggerType.MANUAL,
            triggerConfig = TriggerConfig.ManualTrigger(
                stateRequired = StateType.ACELERADO
            ),
            action = "1 minuto de pausa + 3 respiraciones lentas",
            durationMinutes = 2,
            priority = Priority.ALTA,
            minimumAction = null,
            minimumDurationMinutes = null
        ),
        
        // === APRENDIZAJE ===
        Rule(
            name = "Sprint IA",
            category = Category.APRENDIZAJE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(
                hour = 7,
                minute = 30,
                daysOfWeek = listOf(1, 4) // Lunes y Jueves
            ),
            action = "Sprint IA 45 min: leer, probar, reflexionar",
            durationMinutes = 45,
            priority = Priority.MEDIA,
            minimumAction = "20 min de lectura enfocada",
            minimumDurationMinutes = 20
        ),
        
        Rule(
            name = "Stop consumo IA",
            category = Category.APRENDIZAJE,
            triggerType = TriggerType.PATTERN,
            triggerConfig = TriggerConfig.PatternTrigger(
                relatedRuleId = null,
                daysWithoutCompletion = 0, // Se evalúa por tiempo en sesión
                category = Category.APRENDIZAJE
            ),
            action = "Escribir: ¿qué cambia esto en mi trabajo?",
            durationMinutes = 10,
            priority = Priority.MEDIA,
            minimumAction = "1 párrafo de reflexión",
            minimumDurationMinutes = 5
        ),
        
        // === PANTALLAS NOCTURNAS ===
        Rule(
            name = "Movilidad pre-series",
            category = Category.CUERPO,
            triggerType = TriggerType.EVENT,
            triggerConfig = TriggerConfig.EventTrigger(eventName = "inicio_series"),
            action = "10 min de movilidad antes de ver series",
            durationMinutes = 10,
            priority = Priority.MEDIA,
            minimumAction = "5 min de estiramientos básicos",
            minimumDurationMinutes = 5
        ),
        
        Rule(
            name = "Cierre nocturno",
            category = Category.MENTE,
            triggerType = TriggerType.TIME,
            triggerConfig = TriggerConfig.TimeTrigger(hour = 22, minute = 30),
            action = "No iniciar nada nuevo. Preparar cierre del día.",
            durationMinutes = 5,
            priority = Priority.MEDIA,
            minimumAction = null,
            minimumDurationMinutes = null
        ),
        
        Rule(
            name = "Reset sueño",
            category = Category.MENTE,
            triggerType = TriggerType.PATTERN,
            triggerConfig = TriggerConfig.PatternTrigger(
                relatedRuleId = null, // Se trackea por hora de último log
                daysWithoutCompletion = 3,
                category = null
            ),
            action = "Noche de reset: leer + dormir antes de las 23:00",
            durationMinutes = 60,
            priority = Priority.ALTA,
            minimumAction = "Dormir máximo a las 23:30",
            minimumDurationMinutes = 30
        )
    )
}
```

---

## 🔍 Sistema de Detección de Patrones

```kotlin
class DetectPatternsUseCase @Inject constructor(
    private val logRepository: LogRepository,
    private val ruleRepository: RuleRepository
) {
    
    suspend fun detectPatterns(): List<PatternSuggestion> {
        val suggestions = mutableListOf<PatternSuggestion>()
        val rules = ruleRepository.getActiveRules()
        
        for (rule in rules) {
            val logs = logRepository.getLogsForRule(rule.id, days = 14)
            
            // Detectar regla que falla consistentemente a cierta hora/día
            val failuresByDay = logs
                .filter { it.status == LogStatus.SKIPPED || it.status == LogStatus.MISSED }
                .groupBy { getDayOfWeek(it.timestamp) }
            
            for ((day, failures) in failuresByDay) {
                if (failures.size >= 3) {
                    suggestions.add(
                        PatternSuggestion(
                            ruleId = rule.id,
                            type = SuggestionType.CHANGE_DAY,
                            message = "\"${rule.name}\" falla ${failures.size} veces los $day. ¿Mover a otro día u hora?",
                            suggestedChange = "Cambiar día o reducir duración"
                        )
                    )
                }
            }
            
            // Detectar si versión mínima se usa mucho
            val minimumCount = logs.count { it.wasMinimum }
            val totalCount = logs.size
            if (totalCount > 5 && minimumCount.toFloat() / totalCount > 0.6f) {
                suggestions.add(
                    PatternSuggestion(
                        ruleId = rule.id,
                        type = SuggestionType.REDUCE_DEFAULT,
                        message = "Usas el mínimo en \"${rule.name}\" el ${(minimumCount * 100 / totalCount)}% del tiempo. ¿Hacer el mínimo el nuevo default?",
                        suggestedChange = "Reducir duración default a ${rule.minimumDurationMinutes} min"
                    )
                )
            }
        }
        
        return suggestions
    }
}

data class PatternSuggestion(
    val ruleId: String,
    val type: SuggestionType,
    val message: String,
    val suggestedChange: String
)

enum class SuggestionType {
    CHANGE_DAY,
    CHANGE_TIME,
    REDUCE_DEFAULT,
    DEACTIVATE
}
```

---

## 📅 Integración con Calendario

```kotlin
class CalendarRepository @Inject constructor(
    private val context: Context
) {
    
    private val contentResolver: ContentResolver = context.contentResolver
    
    /**
     * Verifica si el usuario está libre desde 'from' hasta 'until'
     */
    suspend fun isFreeUntil(from: LocalDateTime, until: LocalDateTime): Boolean {
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} < ?"
        val selectionArgs = arrayOf(
            from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            until.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString()
        )
        
        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        
        val hasEvents = cursor?.use { it.count > 0 } ?: false
        return !hasEvents
    }
    
    /**
     * Obtiene la próxima reunión del día
     */
    suspend fun getNextMeeting(): CalendarEvent? {
        // Implementación similar
    }
}

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
```

---

## 🚀 Instrucciones de Implementación para Claude Code

### Paso 1: Setup Inicial

```bash
# En VS Code, abre terminal y navega a tu carpeta de proyectos
cd ~/AndroidStudioProjects

# Crea el proyecto (o hazlo desde Android Studio primero)
# Si usas Android Studio: File > New > New Project > Empty Compose Activity
# Nombre: IFThenApp
# Package: com.ifthen.app
# Min SDK: 26

# Luego abre la carpeta en VS Code
code IFThenApp

# Inicia Claude Code
claude
```

### Paso 2: Prompt Inicial para Claude Code

```
Estoy creando una app Android llamada "IF-THEN Personal System". 
Lee el archivo IF-THEN-APP-SPEC.md que contiene toda la especificación.

Empieza por:
1. Configurar build.gradle.kts con todas las dependencias necesarias (Hilt, Room, Compose, WorkManager)
2. Crear la estructura de carpetas según la spec
3. Implementar los modelos de datos (entities y domain models)
4. Configurar Room database con los DAOs

Hazlo paso a paso, confirmando cada fase antes de continuar.
```

### Paso 3: Secuencia de Desarrollo

1. **Fase 1 - Base (Día 1-2)**
   - Configurar dependencias
   - Crear modelos de datos
   - Configurar Room DB
   - Implementar repositorios básicos

2. **Fase 2 - Motor de Reglas (Día 3-4)**
   - Implementar RuleEngine
   - Crear sistema de scheduling con AlarmManager
   - Integrar WorkManager para tareas en background

3. **Fase 3 - Notificaciones (Día 5-6)**
   - Crear canales de notificación
   - Implementar NotificationBuilder
   - Crear BroadcastReceiver para acciones

4. **Fase 4 - UI (Día 7-9)**
   - Implementar TodayScreen
   - Implementar RulesScreen
   - Implementar WeekScreen
   - Crear navegación

5. **Fase 5 - Integración Calendario (Día 10-11)**
   - Permisos de calendario
   - CalendarRepository
   - Integrar con RuleEngine

6. **Fase 6 - Patrones y Polish (Día 12-14)**
   - DetectPatternsUseCase
   - Seed data con reglas default
   - Testing y ajustes

### Paso 4: Comandos Útiles durante Desarrollo

```bash
# Compilar y verificar errores
./gradlew assembleDebug

# Correr en emulador/dispositivo
./gradlew installDebug

# Ver logs
adb logcat | grep "IFThen"
```

---

## ⚠️ Permisos Requeridos (AndroidManifest.xml)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Calendario -->
    <uses-permission android:name="android.permission.READ_CALENDAR" />
    
    <!-- Notificaciones -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
    
    <!-- Vibración para notificaciones -->
    <uses-permission android:name="android.permission.VIBRATE" />
    
    <application
        android:name=".IFThenApplication"
        ...>
        
        <!-- Receiver para alarmas -->
        <receiver
            android:name=".scheduler.AlarmReceiver"
            android:exported="false" />
        
        <!-- Receiver para acciones de notificación -->
        <receiver
            android:name=".notification.NotificationActionReceiver"
            android:exported="false" />
        
        <!-- Receiver para reinicio del dispositivo -->
        <receiver
            android:name=".scheduler.BootReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
        
        <!-- WorkManager Worker -->
        <service
            android:name="androidx.work.impl.foreground.SystemForegroundService"
            android:foregroundServiceType="dataSync" />
            
    </application>
</manifest>
```

---

## 📦 Dependencias (build.gradle.kts app level)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.ifthen.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ifthen.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}
```

---

## ✅ Checklist de Validación

Antes de considerar el MVP completo, verificar:

- [ ] Las notificaciones se disparan a la hora correcta
- [ ] Las acciones de notificación (HECHO/SALTAR/MÍNIMO) funcionan
- [ ] La integración con calendario detecta reuniones correctamente
- [ ] El estado del usuario (Día pesado, etc.) afecta las reglas
- [ ] Los logs se guardan correctamente
- [ ] La vista semanal muestra % real
- [ ] Las reglas pre-cargadas aparecen en primera ejecución
- [ ] La app sobrevive a reinicios del dispositivo
- [ ] Los patrones se detectan después de 1-2 semanas de uso

---

## 🎯 Criterios de Éxito

La app está lista cuando:

1. **Reduce fricción**: El usuario no tiene que pensar qué hacer
2. **Es silenciosa**: Solo notifica cuando hay acción pendiente
3. **No juzga**: Registra sin culpar
4. **Aprende**: Sugiere ajustes basados en datos reales
5. **Persiste**: Funciona día tras día sin mantenimiento

---

*Documento generado para implementación con Claude Code en VS Code*
*Versión: 1.0 | Fecha: Enero 2025*