package com.crm.chatflow.ui.screens

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
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
import com.crm.chatflow.ui.viewmodel.ChatViewModel
import com.crm.chatflow.utils.ReceiptHelper
import com.crm.chatflow.utils.WhatsAppHelper

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val viewModel = remember(context) {
        val activity = context as ComponentActivity
        val application = activity.application
        ViewModelProvider(
            activity,
            ChatViewModel.Factory(application)
        )[ChatViewModel::class.java]
    }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val cardBgLight = Color(0xFF1E283C)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val accentGreen = Color(0xFF34D399)

    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val listState = rememberLazyListState()

    // Auto-scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Asistente IA",
                style = MaterialTheme.typography.headlineMedium,
                color = textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Escribe un comando, ej: 'Venta de 50 por diseño'",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(messages) { message ->
                            ChatBubble(
                                text = message.text,
                                isUser = message.isUser,
                                cardBg = cardBg,
                                textPrimary = textPrimary,
                                accentGreen = accentGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.inputText.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe una orden a la IA...", color = textSecondary) },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = accentGreen,
                        unfocusedBorderColor = cardBgLight,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    ),
                    maxLines = 3
                )

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isProcessing) {
                            viewModel.sendMessage(inputText)
                        }
                    },
                    containerColor = accentGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    cardBg: Color,
    textPrimary: Color,
    accentGreen: Color
) {
    val context = LocalContext.current
    val bubbleColor = if (isUser) Color(0xFF1E283C) else cardBg
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = text,
                    color = textPrimary,
                    fontSize = 14.sp
                )

                if (!isUser && (text.contains("registrado") || text.contains("venta") || text.contains("$"))) {
                    Divider(color = Color(0xFF2B364C), thickness = 1.dp)

                    Button(
                        onClick = {
                            val receiptText = ReceiptHelper.generateReceiptText(
                                title = "Transacción vía Chat IA",
                                amount = 50.0,
                                type = "INCOME"
                            )
                            WhatsAppHelper.sendMessage(context, "", receiptText)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Enviar Recibo por WhatsApp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
