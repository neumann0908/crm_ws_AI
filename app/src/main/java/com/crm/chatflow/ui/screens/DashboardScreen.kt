package com.crm.chatflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crm.chatflow.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToClients: () -> Unit,
    onNavigateToCalculator: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkDeep)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera de bienvenida estilo Bento
            Text(
                text = "ChatFlow CRM",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "Panel principal • Offline First",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta Bento Principal (Métrica de Leads)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BentoCard)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LEADS ACTIVOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "12",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            // Fila de tarjetas secundarias (Bento Grid Grid layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de Cotizador
                Card(
                    onClick = onNavigateToCalculator,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCard)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomStart) {
                        Text(
                            text = "Cotizador Express",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }

                // Tarjeta de Clientes / Kanban
                Card(
                    onClick = onNavigateToClients,
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCard)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomStart) {
                        Text(
                            text = "Embudo Kanban",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }
                }
            }
        }
    }
}
