package com.escala.ministerial.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.escala.ministerial.feature.auditoria.presentation.AuditoriaScreen
import com.escala.ministerial.feature.dashboard.presentation.DashboardScreen
import com.escala.ministerial.feature.escalas.presentation.EscalasScreen
import com.escala.ministerial.feature.eventos.presentation.EventosScreen
import com.escala.ministerial.feature.feedback.presentation.FeedbackScreen
import com.escala.ministerial.feature.ministros.presentation.MinistrosScreen

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route,
)

private val navItems = listOf(
    NavItem("Dashboard", Icons.Default.Dashboard, Route.Dashboard),
    NavItem("Ministros", Icons.Default.Group, Route.Ministros),
    NavItem("Eventos", Icons.Default.CalendarMonth, Route.Eventos),
    NavItem("Escalas", Icons.Default.Schedule, Route.Escalas),
    NavItem("Feedback", Icons.Default.Feedback, Route.Feedback),
    NavItem("Auditoria", Icons.Default.Assessment, Route.Auditoria),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
            ) {
                navItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Dashboard> { DashboardScreen() }
            composable<Route.Ministros> { MinistrosScreen() }
            composable<Route.Eventos> { EventosScreen() }
            composable<Route.Escalas> { EscalasScreen() }
            composable<Route.Feedback> { FeedbackScreen() }
            composable<Route.Auditoria> { AuditoriaScreen() }
        }
    }
}
