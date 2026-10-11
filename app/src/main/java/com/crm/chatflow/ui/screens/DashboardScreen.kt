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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
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
import com.crm.chatflow.data.local.entity.TransactionEntity
import com.crm.chatflow.ui.viewmodel.DashboardViewModel
import com.crm.chatflow.ui.viewmodel.TransactionFilter
import com.crm.chatflow.utils.ReceiptHelper
import com.crm.chatflow.utils.WhatsAppHelper

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val viewModel = remember(context) {
        val activity = context as ComponentActivity
        val application = activity.application
        ViewModelProvider(
            activity,
            DashboardViewModel.Factory(application)
        )[DashboardViewModel::class.java]
    }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val cardBgLight = Color(0xFF1E283C)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val accentGreen = Color(0xFF34D399)
    val accentRed = Color(0xFFF87171)

    val transactions by viewModel.transactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.selectedFilter.collectAsState()

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
                    Icon(Icons.Default.Add, contentDescription = "Nueva Transacción")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Mi Panel",
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
                    placeholder = { Text("Buscar movimiento...", color = textSecondary) },
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

                // Filtros Rápido por Tipo
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = activeFilter == TransactionFilter.ALL,
                        onClick = { viewModel.selectedFilter.value = TransactionFilter.ALL },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = activeFilter == TransactionFilter.INCOME,
                        onClick = { viewModel.selectedFilter.value = TransactionFilter.INCOME },
                        label = { Text("Ingresos") }
                    )
                    FilterChip(
                        selected = activeFilter == TransactionFilter.EXPENSE,
                        onClick = { viewModel.selectedFilter.value = TransactionFilter.EXPENSE },
                        label = { Text("Gastos") }
                    )
                }

                Text(
                    text = "Movimientos",
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay movimientos registrados." else "No se encontraron resultados.",
                            color = textSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(transactions, key = { it.id }) { item ->
                            TransactionItemCard(
                                transaction = item,
                                cardBg = cardBg,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                accentGreen = accentGreen,
                                accentRed = accentRed
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, type ->
                viewModel.addTransaction(title, amount, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentGreen: Color,
    accentRed: Color
) {
    val context = LocalContext.current
    val isIncome = transaction.type == "INCOME"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isIncome) accentGreen.copy(alpha = 0.15f) else accentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isIncome) accentGreen else accentRed
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.title,
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isIncome) "Venta" else "Gasto",
                        color = textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isIncome) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    color = if (isIncome) accentGreen else accentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val receiptText = ReceiptHelper.generateReceiptText(
                            title = transaction.title,
                            amount = transaction.amount,
                            type = transaction.type
                        )
                        WhatsAppHelper.sendMessage(context, "", receiptText)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir Recibo",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INCOME") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Transacción", color = Color.White) },
        containerColor = Color(0xFF161E2E),
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Concepto") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto ($)") },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = { type = "INCOME" },
                        label = { Text("Ingreso") }
                    )
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = { type = "EXPENSE" },
                        label = { Text("Gasto") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        onConfirm(title, amount, type)
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
