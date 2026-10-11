package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val clientName: String,
    val clientPhone: String,
    val concept: String,
    val amount: Double,
    val dueDate: String,
    val type: String, // "COBRO" o "CITA"
    val isCompleted: Boolean = false
)
