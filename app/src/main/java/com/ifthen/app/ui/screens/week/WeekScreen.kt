package com.ifthen.app.ui.screens.week

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ifthen.app.ui.components.CompletionProgressCard
import com.ifthen.app.ui.components.PatternSuggestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekScreen(
    viewModel: WeekViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Esta semana") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CompletionProgressCard(
                        percentage = uiState.completionPercentage,
                        label = "Cumplimiento semanal"
                    )
                }

                item {
                    DailyStatsCard(dailyStats = uiState.dailyStats)
                }

                item {
                    AlcoholCountCard(count = uiState.alcoholCount)
                }

                if (uiState.priorityStats.total > 0) {
                    item {
                        PriorityStatsCard(stats = uiState.priorityStats)
                    }
                }

                if (uiState.worstDay != null) {
                    item {
                        WorstDayCard(day = uiState.worstDay!!)
                    }
                }

                if (uiState.suggestions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Sugerencias",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(uiState.suggestions) { suggestion ->
                        PatternSuggestionCard(
                            suggestion = suggestion,
                            onAccept = { viewModel.applySuggestion(it) },
                            onDismiss = { viewModel.dismissSuggestion(it) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DailyStatsCard(
    dailyStats: List<DayStats>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Por dia",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyStats.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = day.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (day.percentage >= 0) "${day.percentage}%" else "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                day.percentage < 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                                day.percentage < 50 -> MaterialTheme.colorScheme.error
                                day.percentage < 80 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorstDayCard(
    day: DayStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "\uD83D\uDCC9")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${day.name} bajo (${day.percentage}%)",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AlcoholCountCard(
    count: Int,
    modifier: Modifier = Modifier
) {
    val isOverLimit = count > 3
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverLimit) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "\uD83C\uDF77") // 🍷
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Alcohol: $count/3 copas",
                color = if (isOverLimit) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PriorityStatsCard(
    stats: PriorityStats,
    modifier: Modifier = Modifier
) {
    val completionRate = if (stats.total > 0) {
        ((stats.completed + stats.delegated) * 100) / stats.total
    } else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "\uD83C\uDFAF") // 🎯
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Prioridades: $completionRate% cumplidas",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "\u2705 ${stats.completed}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "\uD83D\uDCE4 ${stats.delegated}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "\u23F3 ${stats.pending}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
