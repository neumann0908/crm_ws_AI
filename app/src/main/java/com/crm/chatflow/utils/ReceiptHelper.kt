package com.crm.chatflow.utils

import com.crm.chatflow.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptHelper {

    fun generateReceiptText(
        transaction: TransactionEntity,
        clientName: String = "Cliente",
        businessName: String = "Ethereal CRM"
    ): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(transaction.timestamp))
        val isIncome = transaction.type == "INCOME"
        val header = if (isIncome) "🧾 COMPROBANTE DE PAGO" else "🧾 COMPROBANTE DE GASTO"
        val symbol = if (isIncome) "+" else "-"

        return """
            $header
            --------------------------------
            🏢 $businessName
            📅 Fecha: $dateStr
            👤 Cliente / Concepto: $clientName
            --------------------------------
            📝 Detalle: ${transaction.title}
            💵 Monto: $symbol$${String.format("%.2f", transaction.amount)}
            --------------------------------
            ¡Gracias por preferir nuestros servicios!
        """.trimIndent()
    }
}
