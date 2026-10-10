package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotations")
data class QuotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val clientId: Long,
    val totalAmount: Double,
    val itemsJson: String, // Detalle de los productos seleccionados en formato JSON
    val createdAt: Long
)
