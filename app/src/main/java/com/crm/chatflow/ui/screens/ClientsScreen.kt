package com.crm.chatflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.ui.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    viewModel: ClientViewModel = viewModel(factory = ClientViewModel.Factory(LocalContext.current.applicationContext as android.app.Application))
) {
    val clients by viewModel.clients.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)

    Scaffold(
        containerColor = darkBg,
        topBar = { TopAppBar(title = { Text("Gestión de Clientes", color = Color.White, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = accentGreen, contentColor = darkBg) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cliente")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(darkBg)) {
            if (clients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay clientes registrados", color = Color(0xFF94A3B8))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(clients) { client -> ClientItem(client = client, onDelete = { viewModel.deleteClient(client) }) }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = cardBg,
                    title = { Text("Nuevo Cliente", color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono / WhatsApp") })
                            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") })
                            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas / Observaciones") })
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (name.isNotBlank()) {
                                viewModel.addClient(name, phone, email, notes)
                                name = ""; phone = ""; email = ""; notes = ""; showDialog = false
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = accentGreen)) { Text("Guardar", color = darkBg) }
                    },
                    dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar", color = Color(0xFF94A3B8)) } }
                )
            }
        }
    }
}

@Composable
fun ClientItem(client: ClientEntity, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(client.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Tel: ${client.phone}", color = Color(0xFF94A3B8), fontSize = 14.sp)
                if (client.notes.isNotBlank()) Text("Nota: ${client.notes}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444)) }
        }
    }
}
