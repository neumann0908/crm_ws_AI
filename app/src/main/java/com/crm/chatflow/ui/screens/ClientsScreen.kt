package com.crm.chatflow.ui.screens

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.ui.viewmodel.ClientViewModel
import com.crm.chatflow.utils.WhatsAppHelper

@Composable
fun ClientsScreen() {
    val context = LocalContext.current
    val viewModel = remember(context) {
        val activity = context as ComponentActivity
        val application = activity.application
        ViewModelProvider(
            activity,
            ClientViewModel.Factory(application)
        )[ClientViewModel::class.java]
    }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val cardBgLight = Color(0xFF1E283C)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val accentGreen = Color(0xFF34D399)

    val clients by viewModel.clients.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBg
    ) {
        Scaffold(
            containerColor = darkBg,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = accentGreen,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Cliente")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Clientes",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Buscador
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar cliente por nombre o email...", color = textSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textSecondary) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = accentGreen,
                        unfocusedBorderColor = cardBgLight,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    )
                )

                // Lista de Clientes
                if (clients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay clientes registrados." else "No se encontraron resultados.",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(clients, key = { it.id }) { client ->
                            ClientItemCard(
                                client = client,
                                cardBg = cardBg,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                accentGreen = accentGreen
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo para agregar cliente
    if (showAddDialog) {
        AddClientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, email, phone, status, notes ->
                viewModel.addClient(name, email, phone, status, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ClientItemCard(
    client: ClientEntity,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentGreen: Color
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E283C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = client.name, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (client.email.isNotBlank()) {
                            Text(text = client.email, color = textSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (client.status == "Activo") Color(0xFF059669).copy(alpha = 0.2f) else Color(0xFFEAB308).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = client.status,
                        color = if (client.status == "Activo") Color(0xFF34D399) else Color(0xFFFACC15),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (client.phone.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = client.phone, color = textSecondary, fontSize = 13.sp)
                    }

                    // Botón para contactar por WhatsApp
                    Button(
                        onClick = {
                            val defaultMessage = "¡Hola ${client.name}! Te escribo desde el CRM de negocio. ¿En qué te podemos ayudar hoy?"
                            WhatsAppHelper.sendMessage(context, client.phone, defaultMessage)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WhatsApp",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (client.notes.isNotBlank()) {
                Text(text = client.notes, color = textSecondary.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AddClientDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Activo") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Cliente", color = Color.White) },
        containerColor = Color(0xFF161E2E),
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, email, phone, status, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399))
            ) {
                Text("Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
}
