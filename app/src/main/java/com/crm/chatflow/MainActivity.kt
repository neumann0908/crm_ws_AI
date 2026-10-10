package com.crm.chatflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.crm.chatflow.ui.screens.DashboardScreen
import com.crm.chatflow.ui.theme.ChatFlowTheme
import com.crm.chatflow.ui.theme.DarkDeep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkDeep
                ) {
                    DashboardScreen(
                        onNavigateToClients = {
                            // Aquí conectaremos la pantalla del Embudo Kanban próximamente
                        },
                        onNavigateToCalculator = {
                            // Aquí conectaremos el Cotizador Express próximamente
                        }
                    )
                }
            }
        }
    }
}
