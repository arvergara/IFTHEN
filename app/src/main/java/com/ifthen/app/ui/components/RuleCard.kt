package com.ifthen.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.Rule
import com.ifthen.app.ui.theme.CompletedColor
import com.ifthen.app.ui.theme.MinimumColor
import com.ifthen.app.ui.theme.SkippedColor

@Composable
fun RuleCard(
    rule: Rule,
    status: LogStatus?,
    onComplete: (String) -> Unit,
    onSkip: (String) -> Unit,
    onMinimum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryEmoji = rule.category.getEmoji()

    val statusIcon = when (status) {
        LogStatus.COMPLETED -> "\u2713"
        LogStatus.MINIMUM -> "\u00BD"
        LogStatus.SKIPPED -> "\u2013"
        LogStatus.MISSED -> "\u2717"
        null -> "\u00B7"
    }

    val statusColor = when (status) {
        LogStatus.COMPLETED -> CompletedColor
        LogStatus.MINIMUM -> MinimumColor
        LogStatus.SKIPPED -> SkippedColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (status != null) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryEmoji,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${rule.durationMinutes}'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(onClick = { onComplete(rule.id) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completar",
                        tint = if (status == LogStatus.COMPLETED) CompletedColor else CompletedColor.copy(alpha = 0.4f)
                    )
                }
                if (rule.minimumAction != null) {
                    IconButton(onClick = { onMinimum(rule.id) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Minimo",
                            tint = if (status == LogStatus.MINIMUM) MinimumColor else MinimumColor.copy(alpha = 0.4f)
                        )
                    }
                }
                IconButton(onClick = { onSkip(rule.id) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Saltar",
                        tint = if (status == LogStatus.SKIPPED) SkippedColor else SkippedColor.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
