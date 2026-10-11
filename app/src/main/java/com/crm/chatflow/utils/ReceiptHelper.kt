package com.crm.chatflow.utils

import com.crm.chatflow.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptHelper {

    fun generateReceiptText(
        title: String,
        amount: Double,
        type: String,
        clientName: String = "Cliente",
        businessName: String = "Ethereal CRM"
    ): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())
        val isIncome = type == "INCOME"
        val header = if (isIncome) "🧾 COMPROBANTE DE PAGO" else "🧾 COMPROBANTE DE GASTO"
        val symbol = if (isIncome) "+" else "-"

        return """
            $header
            --------------------------------
            🏢 $businessName
            📅 Fecha: $dateStr
            👤 Concepto / Cliente: $clientName
            --------------------------------
            📝 Detalle: $title
            💵 Monto: $symbol$${String.format("%.2f", amount)}
            --------------------------------
            ¡Gracias por preferir nuestros servicios!
        """.trimIndent()
    }
}
