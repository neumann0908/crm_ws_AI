package com.crm.chatflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.ui.theme.*

@Composable
fun ClientsScreen(
    clients: List<ClientEntity>,
    onClientClick: (ClientEntity) -> Unit,
    onWhatsAppClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkDeep)
            .padding(16.dp)
    ) {
        Text(
            text = "Embudo de Ventas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Gestiona tus prospectos por etapa",
            fontSize = 13.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clients) { client ->
                ClientBentoCard(
                    client = client,
                    onClick = { onClientClick(client) },
                    onWhatsAppClick = { onWhatsAppClick(client.phone) }
                )
            }
        }
    }
}

@Composable
fun ClientBentoCard(
    client: ClientEntity,
    onClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BentoCard)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = client.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                // Insignia de estado neón
                Text(
                    text = client.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (client.status) {
                        "LEAD" -> NeonYellow
                        "QUOTED" -> NeonPurple
                        else -> NeonGreen
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (client.notes.isNotEmpty()) client.notes else "Sin notas adicionales",
                fontSize = 13.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de acción rápida para lanzar WhatsApp
            Button(
                onClick = onWhatsAppClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Abrir WhatsApp",
                    color = DarkDeep,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
