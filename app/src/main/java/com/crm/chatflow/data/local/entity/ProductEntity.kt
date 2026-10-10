package com.crm.chatflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val price: Double,
    val cost: Double = 0.0, // Útil para el enfoque de márgenes y costes
    val category: String = "General",
    val isActive: Boolean = true
)
