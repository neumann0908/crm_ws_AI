package com.crm.chatflow.ai

import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.TransactionEntity

sealed class ParsedCommand {
    data class AddTransaction(val transaction: TransactionEntity) : ParsedCommand()
    data class AddClient(val client: ClientEntity) : ParsedCommand()
    data class Query(val intent: String) : ParsedCommand()
    object Unknown : ParsedCommand()
}

class LocalAiParser {

    fun parseInput(input: String): ParsedCommand {
        val cleanInput = input.trim()

        // 1. Detección de Transacciones (Ventas / Gastos)
        val isIncome = cleanInput.contains("venta", ignoreCase = true) || cleanInput.contains("ingreso", ignoreCase = true) || cleanInput.contains("cobre", ignoreCase = true)
        val isExpense = cleanInput.contains("gasto", ignoreCase = true) || cleanInput.contains("pague", ignoreCase = true) || cleanInput.contains("compras", ignoreCase = true)

        if (isIncome || isExpense) {
            val amountRegex = Regex("""(?i)(?:USD|\$|\b)?\s*(\d+(?:[\.,]\d+)?)\s*(?:USD|\$|dolares|dólares)?""")
            val match = amountRegex.find(cleanInput)
            val amount = match?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull()

            if (amount != null && amount > 0) {
                val type = if (isIncome) "INCOME" else "EXPENSE"
                val title = if (isIncome) "Venta vía IA Chat" else "Gasto vía IA Chat"
                return ParsedCommand.AddTransaction(
                    TransactionEntity(
                        title = title,
                        amount = amount,
                        type = type
                    )
                )
            }
        }

        // 2. Detección de Agregar Cliente
        val isClient = cleanInput.contains("cliente", ignoreCase = true) || cleanInput.contains("agregar a", ignoreCase = true) || cleanInput.contains("registrar a", ignoreCase = true)
        if (isClient) {
            val phoneRegex = Regex("""(\+?\d{8,15})""")
            val phoneMatch = phoneRegex.find(cleanInput)
            val phone = phoneMatch?.value ?: ""

            // Extraer un nombre aproximado
            val words = cleanInput.split(" ")
            val nameCandidates = words.filter { !listOf("cliente", "agregar", "registrar", "a", "con", "tel", "telefono", "teléfono", "nuevo", "nueva").contains(it.lowercase()) && !it.matches(Regex("""\d+""")) }
            val name = nameCandidates.take(2).joinToString(" ").replaceFirstChar { it.uppercase() }

            if (name.isNotBlank()) {
                return ParsedCommand.AddClient(
                    ClientEntity(
                        name = name,
                        email = "",
                        phone = phone,
                        status = "Activo",
                        notes = "Registrado desde Asistente IA"
                    )
                )
            }
        }

        // 3. Consultas Generales
        if (cleanInput.contains("balance", ignoreCase = true) || cleanInput.contains("cuanto", ignoreCase = true) || cleanInput.contains("resumen", ignoreCase = true)) {
            return ParsedCommand.Query("balance_summary")
        }

        return ParsedCommand.Unknown
    }
}
