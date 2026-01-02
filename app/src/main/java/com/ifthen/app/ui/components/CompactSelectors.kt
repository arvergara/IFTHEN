package com.ifthen.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifthen.app.domain.model.ModeType
import com.ifthen.app.domain.model.StateType

@Composable
fun CompactSelectors(
    currentMode: ModeType,
    currentState: StateType,
    onModeChange: (ModeType) -> Unit,
    onStateChange: (StateType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompactDropdown(
            label = currentMode.getEmoji(),
            value = currentMode.getDisplayName(),
            modifier = Modifier.weight(1f)
        ) { onDismiss ->
            ModeType.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text("${mode.getEmoji()} ${mode.getDisplayName()}") },
                    onClick = {
                        onModeChange(mode)
                        onDismiss()
                    }
                )
            }
        }

        CompactDropdown(
            label = "",
            value = currentState.getDisplayName(),
            modifier = Modifier.weight(1f)
        ) { onDismiss ->
            StateType.entries.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state.getDisplayName()) },
                    onClick = {
                        onStateChange(state)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun CompactDropdown(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    content: @Composable (onDismiss: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (label.isNotEmpty()) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            content { expanded = false }
        }
    }
}
