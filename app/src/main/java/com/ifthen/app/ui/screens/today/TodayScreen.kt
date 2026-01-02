package com.ifthen.app.ui.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ifthen.app.ui.components.CompactSelectors
import com.ifthen.app.ui.components.RuleCard
import com.ifthen.app.ui.screens.priorities.PrioritiesInputDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onNavigateToDelegates: () -> Unit = {},
    onNavigateToDelegationFlow: () -> Unit = {},
    onNavigateToPrioritiesReview: () -> Unit = {},
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPrioritiesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.dateFormatted,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    Button(
                        onClick = onNavigateToDelegationFlow,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("Delegar")
                    }
                    IconButton(onClick = onNavigateToDelegates) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configurar delegados"
                        )
                    }
                },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                CompactSelectors(
                    currentMode = uiState.currentMode,
                    currentState = uiState.currentState,
                    onModeChange = { viewModel.updateMode(it) },
                    onStateChange = { viewModel.updateState(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.todayRules.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay reglas activas para hoy",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            items = uiState.todayRules,
                            key = { it.rule.id }
                        ) { ruleWithStatus ->
                            RuleCard(
                                rule = ruleWithStatus.rule,
                                status = ruleWithStatus.status,
                                onComplete = { ruleId ->
                                    when (ruleId) {
                                        "rule_trabajo_prioridades" -> {
                                            showPrioritiesDialog = true
                                        }
                                        "rule_trabajo_cumplimiento" -> {
                                            viewModel.markComplete(ruleId)
                                            onNavigateToPrioritiesReview()
                                        }
                                        else -> viewModel.markComplete(ruleId)
                                    }
                                },
                                onSkip = { viewModel.markSkipped(it) },
                                onMinimum = { viewModel.markMinimum(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPrioritiesDialog) {
        PrioritiesInputDialog(
            existingPriorities = uiState.todayPriorities,
            onDismiss = { showPrioritiesDialog = false },
            onSave = { p1, p2, p3 ->
                viewModel.savePriorities(p1, p2, p3)
                viewModel.markComplete("rule_trabajo_prioridades")
                showPrioritiesDialog = false
            }
        )
    }
}
