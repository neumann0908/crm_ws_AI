package com.crm.chatflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.crm.chatflow.ui.viewmodel.ChatMessage
import com.crm.chatflow.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory(LocalContext.current.applicationContext as android.app.Application))
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)

    Scaffold(
        containerColor = darkBg,
        topBar = { TopAppBar(title = { Text("Asistente IA (CRM)", color = Color.White, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)) },
        bottomBar = {
            Surface(color = cardBg, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.inputText.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe para registrar...", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentGreen, unfocusedBorderColor = Color(0xFF334155), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        maxLines = 3
                    )
                    IconButton(onClick = { if (inputText.isNotBlank() && !isProcessing) viewModel.sendMessage(inputText) }, enabled = !isProcessing) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = accentGreen)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(darkBg)) {
            if (messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ej: 'Agrega a Juan con teléfono 04141234567' o 'Venta de 50'", color = Color(0xFF94A3B8), modifier = Modifier.padding(32.dp))
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(messages) { message -> MessageBubble(message) }
                }
                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) { coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) } }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) Color(0xFF1E283C) else Color(0xFF161E2E)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(color = bubbleColor, shape = MaterialTheme.shapes.medium, modifier = Modifier.widthIn(max = 280.dp)) {
            Text(text = message.text, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
        }
    }
}
