package com.crm.chatflow.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crm.chatflow.data.local.entity.ReminderEntity
import com.crm.chatflow.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: ReminderViewModel = viewModel(factory = ReminderViewModel.Factory(LocalContext.current.applicationContext as android.app.Application))
) {
    val reminders by viewModel.reminders.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("COBRO") }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)

    Scaffold(
        containerColor = darkBg,
        topBar = {
            TopAppBar(
                title = { Text("Agenda y Cobros", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = accentGreen,
                contentColor = darkBg
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Recordatorio")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(darkBg)) {
            if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay recordatorios o cobros pendientes", color = Color(0xFF94A3B8))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggleComplete = { viewModel.toggleComplete(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) },
                            onWhatsAppClick = {
                                val phoneClean = reminder.clientPhone.replace("+", "").trim()
                                val message = if (reminder.type == "COBRO") {
                                    "Hola ${reminder.clientName}, te recordamos que tienes un cobro pendiente por concepto de '${reminder.concept}' por un monto de \$${reminder.amount}. Fecha límite: ${reminder.dueDate}. ¡Gracias!"
                                } else {
                                    "Hola ${reminder.clientName}, te recordamos nuestra cita programada para el día ${reminder.dueDate}: ${reminder.concept}. ¡Te esperamos!"
                                }
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$phoneClean&text=${Uri.encode(message)}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = cardBg,
                    title = { Text("Nuevo Recordatorio", color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nombre del Cliente") })
                            OutlinedTextField(value = clientPhone, onValueChange = { clientPhone = it }, label = { Text("Teléfono WhatsApp") })
                            OutlinedTextField(value = concept, onValueChange = { concept = it }, label = { Text("Concepto / Servicio") })
                            OutlinedTextField(value = amountStr, onValueChange = { amountStr = it }, label = { Text("Monto ($)") })
                            OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Fecha Límite / Cita") })
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = { type = "COBRO" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "COBRO") accentGreen else Color.Gray)) {
                                    Text("Cobro", color = darkBg)
                                }
                                Button(onClick = { type = "CITA" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "CITA") accentGreen else Color.Gray)) {
                                    Text("Cita", color = darkBg)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (clientName.isNotBlank() && concept.isNotBlank()) {
                                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                                    viewModel.addReminder(clientName, clientPhone, concept, amt, dueDate, type)
                                    clientName = ""
                                    clientPhone = ""
                                    concept = ""
                                    amountStr = ""
                                    dueDate = ""
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentGreen)
                        ) {
                            Text("Guardar", color = darkBg)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar", color = Color(0xFF94A3B8))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    val cardBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(reminder.clientName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Badge(containerColor = if (reminder.type == "COBRO") Color(0xFFF59E0B) else accentGreen) {
                    Text(reminder.type, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Concepto: ${reminder.concept}", color = Color(0xFFCBD5E1))
            if (reminder.type == "COBRO") {
                Text("Monto: \$${reminder.amount}", color = accentGreen, fontWeight = FontWeight.Bold)
            }
            Text("Fecha: ${reminder.dueDate}", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onWhatsAppClick) {
                    Text("💬 WhatsApp", color = accentGreen)
                }
                IconButton(onClick = onToggleComplete) {
                    Icon(Icons.Default.Check, contentDescription = "Completar", tint = if (reminder.isCompleted) accentGreen else Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}
