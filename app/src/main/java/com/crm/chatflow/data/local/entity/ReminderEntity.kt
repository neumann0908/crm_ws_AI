package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientName: String,
    val title: String,
    val amount: Double = 0.0,
    val dueDate: String, // Formato "dd/MM/yyyy" o "dd/MM/yyyy HH:mm"
    val type: String,    // "PAYMENT" (Cobro) o "APPOINTMENT" (Cita/Tarea)
    val isCompleted: Boolean = false
)
