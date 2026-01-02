package com.ifthen.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ifthen.app.ui.screens.delegates.DelegatesConfigScreen
import com.ifthen.app.ui.screens.delegates.DelegationFlowScreen
import com.ifthen.app.ui.screens.priorities.PrioritiesReviewScreen
import com.ifthen.app.ui.screens.rules.RuleEditorScreen
import com.ifthen.app.ui.screens.rules.RulesScreen
import com.ifthen.app.ui.screens.today.TodayScreen
import com.ifthen.app.ui.screens.week.WeekScreen

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Today : Screen("today", "Hoy", Icons.Default.Home)
    data object Rules : Screen("rules", "Reglas", Icons.Default.List)
    data object Week : Screen("week", "Semana", Icons.Default.DateRange)
}

object Routes {
    const val RULE_EDITOR = "rule_editor/{ruleId}"
    const val RULE_EDITOR_NEW = "rule_editor/new"
    const val DELEGATES_CONFIG = "delegates_config"
    const val DELEGATION_FLOW = "delegation_flow"
    const val PRIORITIES_REVIEW = "priorities_review"

    fun ruleEditor(ruleId: String?) = if (ruleId != null) "rule_editor/$ruleId" else RULE_EDITOR_NEW
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Today, Screen.Rules, Screen.Week)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    onNavigateToDelegates = {
                        navController.navigate(Routes.DELEGATES_CONFIG)
                    },
                    onNavigateToDelegationFlow = {
                        navController.navigate(Routes.DELEGATION_FLOW)
                    },
                    onNavigateToPrioritiesReview = {
                        navController.navigate(Routes.PRIORITIES_REVIEW)
                    }
                )
            }

            composable(Screen.Rules.route) {
                RulesScreen(
                    onNavigateToEditor = { ruleId ->
                        navController.navigate(Routes.ruleEditor(ruleId))
                    }
                )
            }

            composable(Screen.Week.route) {
                WeekScreen()
            }

            composable(
                route = Routes.RULE_EDITOR,
                arguments = listOf(
                    navArgument("ruleId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                RuleEditorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.RULE_EDITOR_NEW) {
                RuleEditorScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DELEGATES_CONFIG) {
                DelegatesConfigScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DELEGATION_FLOW) {
                DelegationFlowScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onDelegationComplete = { navController.popBackStack() }
                )
            }

            composable(Routes.PRIORITIES_REVIEW) {
                PrioritiesReviewScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
