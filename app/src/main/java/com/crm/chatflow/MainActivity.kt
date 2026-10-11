package com.crm.chatflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crm.chatflow.ui.screens.ChatScreen
import com.crm.chatflow.ui.screens.ClientsScreen
import com.crm.chatflow.ui.screens.DashboardScreen
import com.crm.chatflow.ui.screens.RemindersScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Panel", Icons.Default.Home)
    object Clients : Screen("clients", "Clientes", Icons.Default.Person)
    object Chat : Screen("chat", "Asistente", Icons.Default.Create)
    object Reminders : Screen("reminders", "Agenda", Icons.Default.DateRange)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrmChatFlowTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Clients,
        Screen.Chat,
        Screen.Reminders
    )

    val darkBg = Color(0xFF0B0F19)
    val navBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)
    val textSecondary = Color(0xFF94A3B8)

    Scaffold(
        containerColor = darkBg,
        bottomBar = {
            NavigationBar(
                containerColor = navBg,
                contentColor = Color.White
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 11.sp) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = accentGreen,
                            selectedTextColor = accentGreen,
                            unselectedIconColor = textSecondary,
                            unselectedTextColor = textSecondary,
                            indicatorColor = Color(0xFF1E283C)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Clients.route) { ClientsScreen() }
            composable(Screen.Chat.route) { ChatScreen() }
            composable(Screen.Reminders.route) { RemindersScreen() }
        }
    }
}

@Composable
fun CrmChatFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0B0F19),
            surface = Color(0xFF161E2E),
            primary = Color(0xFF34D399)
        ),
        content = content
    )
}
