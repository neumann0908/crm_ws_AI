package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val phone: String,
    val status: String, // Ej: "LEAD", "QUOTED", "PAID", "CLOSED"
    val lastInteractionDate: Long,
    val notes: String = "",
    val temperature: String = "NORMAL" // Para alertas de leads fríos
)
