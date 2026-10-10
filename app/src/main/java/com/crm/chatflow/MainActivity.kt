package com.crm.chatflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.ui.screens.ClientsScreen
import com.crm.chatflow.ui.screens.DashboardScreen
import com.crm.chatflow.ui.theme.ChatFlowTheme
import com.crm.chatflow.ui.theme.DarkDeep
import com.crm.chatflow.utils.WhatsAppHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkDeep
                ) {
                    var currentScreen by remember { mutableStateOf("dashboard") }

                    // Lista de prueba para el embudo Kanban
                    val dummyClients = listOf(
                        ClientEntity(
                            id = 1L,
                            name = "Carlos Gómez",
                            phone = "+584121234567",
                            status = "LEAD",
                            lastInteractionDate = System.currentTimeMillis(),
                            notes = "Interesado en catálogo de productos",
                            temperature = "WARM"
                        ),
                        ClientEntity(
                            id = 2L,
                            name = "María Rodríguez",
                            phone = "+584149876543",
                            status = "QUOTED",
                            lastInteractionDate = System.currentTimeMillis(),
                            notes = "Presupuesto enviado por WhatsApp",
                            temperature = "HOT"
                        ),
                        ClientEntity(
                            id = 3L,
                            name = "Juan Pérez",
                            phone = "+584245551234",
                            status = "PAID",
                            lastInteractionDate = System.currentTimeMillis(),
                            notes = "Pago confirmado exitosamente",
                            temperature = "NORMAL"
                        )
                    )

                    when (currentScreen) {
                        "dashboard" -> {
                            DashboardScreen(
                                onNavigateToClients = { currentScreen = "kanban" },
                                onNavigateToCalculator = { /* Próximamente Cotizador */ }
                            )
                        }
                        "kanban" -> {
                            ClientsScreen(
                                clients = dummyClients,
                                onClientClick = { client ->
                                    // Detalle o edición de cliente
                                },
                                onWhatsAppClick = { phone ->
                                    WhatsAppHelper.sendMessage(
                                        context = this@MainActivity,
                                        phone = phone,
                                        message = "¡Hola! Te escribo desde ChatFlow CRM."
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
