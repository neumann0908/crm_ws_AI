package com.crm.chatflow.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crm.chatflow.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(application)
    )

    val darkBg = Color(0xFF0B0F19)
    val cardBg = Color(0xFF161E2E)
    val cardBgLight = Color(0xFF1E283C)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)

    val balance by viewModel.totalBalance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(cardBgLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "E",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ethereal",
                            color = textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(cardBg)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(cardBg)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Título principal
            item {
                Text(
                    text = "Mi Panel",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tarjeta de Balance General (Dinámica con Room)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF86EFAC), Color(0xFF34D399), Color(0xFF059669))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Balance total",
                                color = Color(0xFF064E3B),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                            Text(
                                text = String.format("%.2f $", balance),
                                color = Color(0xFF022C22),
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                            Text(
                                text = "Actualizado en tiempo real (Local)",
                                color = Color(0xFF064E3B),
                                fontSize = 12.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.addTransaction("Venta rápida", 150.0, "INCOME") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("+ Venta", color = Color.White, fontSize = 13.sp)
                                }
                                Button(
                                    onClick = { viewModel.addTransaction("Gasto operativo", 45.0, "EXPENSE") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("+ Gasto", color = Color.Black, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Fila de Ingresos y Gastos (Dinámicos)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Ingresos", color = textSecondary, fontSize = 14.sp)
                            Text(
                                text = String.format("+%.2f$", income),
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(text = "Acumulado local", color = textSecondary, fontSize = 11.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Gastos", color = textSecondary, fontSize = 14.sp)
                            Text(
                                text = String.format("-%.2f$", expense),
                                color = textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(text = "Acumulado local", color = textSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Mis Tarjetas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Mis tarjetas", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = cardBgLight),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = textPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Añadir", color = textPrimary, fontSize = 12.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF3B82F6))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("VISA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("**** 6902", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("4156 6727 1439 6902", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Monse Neumann", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                        Text("07/25", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
