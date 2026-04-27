package com.escala.ministerial.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

private const val PREF_FILE = "app_prefs"
private const val PREF_NOTICE = "render_cold_start_notice_seen"

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

private val routeToLabel = mapOf(
    "dashboard" to Route.Dashboard,
    "ministros" to Route.Ministros,
    "eventos"   to Route.Eventos,
    "escalas"   to Route.Escalas,
    "feedback"  to Route.Feedback,
    "auditoria" to Route.Auditoria,
)

@Composable
private fun RenderNoticeDialog() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE) }
    val visible = rememberSaveable { mutableStateOf(!prefs.getBoolean(PREF_NOTICE, false)) }
    if (!visible.value) return
    AlertDialog(
        onDismissRequest = {},
        title = { Text("⏳ Aviso sobre o servidor") },
        text = {
            Text(
                "O servidor está hospedado no plano gratuito do Render, que hiberna após " +
                "15 minutos de inatividade. Na primeira requisição após o sono, pode levar " +
                "até 50 segundos para responder — depois fica rápido.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                prefs.edit().putBoolean(PREF_NOTICE, true).apply()
                visible.value = false
            }) { Text("Entendido") }
        },
    )
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun navigateTo(route: Route) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    RenderNoticeDialog()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                navItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true

                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        selected = isSelected,
                        onClick = { navigateTo(item.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Dashboard,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<Route.Dashboard> {
                DashboardScreen(
                    onNavigate = { key ->
                        routeToLabel[key]?.let { navigateTo(it) }
                    },
                )
            }
            composable<Route.Ministros>  { MinistrosScreen() }
            composable<Route.Eventos>    { EventosScreen() }
            composable<Route.Escalas>    { EscalasScreen() }
            composable<Route.Feedback>   { FeedbackScreen() }
            composable<Route.Auditoria>  { AuditoriaScreen() }
        }
    }
}
