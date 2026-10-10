package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phone: String,
    val status: String, // Ej: "Activo", "Prospecto"
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)
