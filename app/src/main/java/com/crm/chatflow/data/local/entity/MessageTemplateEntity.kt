package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_templates")
data class MessageTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,          // Ej: "Cotización Formal", "Recordatorio de Pago"
    val content: String,        // Contenido con variables (ej: "Hola {nombre}, tu total es {total}")
    val stage: String           // Etapa del Kanban asociada
)
