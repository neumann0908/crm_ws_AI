package com.crm.chatflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.crm.chatflow.data.local.entity.TransactionEntity
import com.crm.chatflow.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(LocalContext.current.applicationContext as android.app.Application))
) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INCOME") }

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val accentGreen = Color(0xFF34D399)
    val accentRed = Color(0xFFEF4444)

    val filteredTransactions = transactions.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = darkBg,
        topBar = {
            TopAppBar(title = { Text("Panel Financiero", color = Color.White, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = accentGreen, contentColor = darkBg) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Transacción")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(darkBg)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard("Ingresos", totalIncome, accentGreen, Modifier.weight(1f))
                    SummaryCard("Gastos", totalExpense, accentRed, Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar transacciones...", color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentGreen, unfocusedBorderColor = Color(0xFF334155), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Text("Movimientos recientes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (filteredTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay transacciones registradas", color = Color(0xFF94A3B8))
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredTransactions) { tx -> TransactionItem(tx) }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    containerColor = cardBg,
                    title = { Text("Nueva Transacción", color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Descripción / Concepto") })
                            OutlinedTextField(value = amountStr, onValueChange = { amountStr = it }, label = { Text("Monto ($)") })
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = { type = "INCOME" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "INCOME") accentGreen else Color.Gray)) { Text("Ingreso", color = darkBg) }
                                Button(onClick = { type = "EXPENSE" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "EXPENSE") accentRed else Color.Gray)) { Text("Gasto", color = darkBg) }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val amount = amountStr.toDoubleOrNull() ?: 0.0
                            if (title.isNotBlank() && amount > 0) {
                                viewModel.addTransaction(title, amount, type)
                                title = ""; amountStr = ""; showDialog = false
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
fun SummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$$amount", color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}

@Composable
fun TransactionItem(tx: TransactionEntity) {
    val isIncome = tx.type == "INCOME"
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(tx.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(tx.date, color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
            Text(text = (if (isIncome) "+ $" else "- $") + tx.amount, color = if (isIncome) Color(0xFF34D399) else Color(0xFFEF4444), fontWeight = FontWeight.Bold)
        }
    }
}
