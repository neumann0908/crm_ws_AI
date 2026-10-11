package com.crm.chatflow.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val textSecondary = Color(0xFF94A3B8)
    val accentGreen = Color(0xFF34D399)

    Scaffold(
        containerColor = darkBg,
        bottomBar = {
            NavigationBar(
                containerColor = cardBg,
                contentColor = textSecondary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Panel") },
                    label = { Text("Panel") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = accentGreen,
                        indicatorColor = accentGreen,
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Clientes") },
                    label = { Text("Clientes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = accentGreen,
                        indicatorColor = accentGreen,
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Asistente") },
                    label = { Text("Asistente") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = accentGreen,
                        indicatorColor = accentGreen,
                        unselectedIconColor = textSecondary,
                        unselectedTextColor = textSecondary
                    )
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            color = darkBg
        ) {
            when (selectedTab) {
                0 -> DashboardScreen()
                1 -> ClientsScreen()
                2 -> ChatScreen()
            }
        }
    }
}
