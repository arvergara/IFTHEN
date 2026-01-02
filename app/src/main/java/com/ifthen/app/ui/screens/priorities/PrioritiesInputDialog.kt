package com.ifthen.app.ui.screens.priorities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifthen.app.domain.model.DailyPriority

@Composable
fun PrioritiesInputDialog(
    existingPriorities: List<DailyPriority> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var priority1 by remember { mutableStateOf(existingPriorities.getOrNull(0)?.text ?: "") }
    var priority2 by remember { mutableStateOf(existingPriorities.getOrNull(1)?.text ?: "") }
    var priority3 by remember { mutableStateOf(existingPriorities.getOrNull(2)?.text ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prioridades del dia") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Que DEBE hacerse hoy?")

                OutlinedTextField(
                    value = priority1,
                    onValueChange = { priority1 = it },
                    label = { Text("1. Prioridad principal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priority2,
                    onValueChange = { priority2 = it },
                    label = { Text("2. Segunda prioridad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priority3,
                    onValueChange = { priority3 = it },
                    label = { Text("3. Tercera prioridad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(priority1, priority2, priority3)
                    onDismiss()
                },
                enabled = priority1.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
