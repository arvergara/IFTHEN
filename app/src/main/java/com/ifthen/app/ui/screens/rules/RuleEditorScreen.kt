package com.ifthen.app.ui.screens.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.Priority
import com.ifthen.app.domain.model.TriggerType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: RuleEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditing) "Editar regla" else "Nueva regla")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveRule() }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Categoria
            CategoryDropdown(
                selectedCategory = uiState.category,
                onCategorySelected = { viewModel.updateCategory(it) }
            )

            // Tipo de trigger
            TriggerTypeDropdown(
                selectedType = uiState.triggerType,
                onTypeSelected = { viewModel.updateTriggerType(it) }
            )

            // Configuracion segun tipo de trigger
            when (uiState.triggerType) {
                TriggerType.TIME, TriggerType.CALENDAR -> {
                    TimeSelector(
                        hour = uiState.hour,
                        minute = uiState.minute,
                        onHourChange = { viewModel.updateHour(it) },
                        onMinuteChange = { viewModel.updateMinute(it) }
                    )
                    DaysOfWeekSelector(
                        selectedDays = uiState.daysOfWeek,
                        onDaysChange = { viewModel.updateDaysOfWeek(it) }
                    )
                }
                TriggerType.EVENT -> {
                    OutlinedTextField(
                        value = uiState.eventName,
                        onValueChange = { viewModel.updateEventName(it) },
                        label = { Text("Nombre del evento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("ej: desayuno_terminado") }
                    )
                }
                else -> {}
            }

            // Accion
            OutlinedTextField(
                value = uiState.action,
                onValueChange = { viewModel.updateAction(it) },
                label = { Text("Accion") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Duracion
            OutlinedTextField(
                value = uiState.durationMinutes.toString(),
                onValueChange = { it.toIntOrNull()?.let { v -> viewModel.updateDuration(v) } },
                label = { Text("Duracion (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Prioridad
            PriorityDropdown(
                selectedPriority = uiState.priority,
                onPrioritySelected = { viewModel.updatePriority(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Modos aplicables
            ApplicableModesSelector(
                selectedModes = uiState.applicableModes,
                onModeToggle = { viewModel.toggleMode(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Version minima (opcional)",
                style = MaterialTheme.typography.titleMedium
            )

            // Accion minima
            OutlinedTextField(
                value = uiState.minimumAction,
                onValueChange = { viewModel.updateMinimumAction(it) },
                label = { Text("Accion minima") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Duracion minima
            OutlinedTextField(
                value = uiState.minimumDurationMinutes.toString(),
                onValueChange = { it.toIntOrNull()?.let { v -> viewModel.updateMinimumDuration(v) } },
                label = { Text("Duracion minima (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = "${selectedCategory.getEmoji()} ${selectedCategory.getDisplayName()}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text("${category.getEmoji()} ${category.getDisplayName()}") },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TriggerTypeDropdown(
    selectedType: TriggerType,
    onTypeSelected: (TriggerType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de activacion") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TriggerType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityDropdown(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedPriority.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Prioridad") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.name) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TimeSelector(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = hour.toString(),
            onValueChange = { it.toIntOrNull()?.takeIf { v -> v in 0..23 }?.let(onHourChange) },
            label = { Text("Hora") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        OutlinedTextField(
            value = minute.toString(),
            onValueChange = { it.toIntOrNull()?.takeIf { v -> v in 0..59 }?.let(onMinuteChange) },
            label = { Text("Minuto") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
private fun DaysOfWeekSelector(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    val dayNames = listOf("L", "M", "X", "J", "V", "S", "D")

    Column {
        Text(
            text = "Dias de la semana",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dayNames.forEachIndexed { index, name ->
                val dayNumber = index + 1
                FilterChip(
                    selected = dayNumber in selectedDays,
                    onClick = {
                        val newDays = if (dayNumber in selectedDays) {
                            selectedDays - dayNumber
                        } else {
                            selectedDays + dayNumber
                        }
                        onDaysChange(newDays.sorted())
                    },
                    label = { Text(name) }
                )
            }
        }
    }
}

@Composable
private fun ApplicableModesSelector(
    selectedModes: List<ModeType>,
    onModeToggle: (ModeType) -> Unit
) {
    Column {
        Text(
            text = "Aplica en modos",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeType.entries.forEach { mode ->
                FilterChip(
                    selected = mode in selectedModes,
                    onClick = { onModeToggle(mode) },
                    label = { Text("${mode.getEmoji()} ${mode.getDisplayName()}") }
                )
            }
        }
    }
}
