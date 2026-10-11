package com.crm.chatflow.ai

import android.content.Context
import android.util.Log
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.TransactionEntity
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File

class LocalAiManager(private val context: Context) {
    private var llmInference: LlmInference? = null

    init {
        try {
            val modelFile = File(context.filesDir, "model.bin")
            if (modelFile.exists()) {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelFile.absolutePath)
                    .setMaxTokens(512)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
            }
        } catch (e: Exception) {
            Log.e("LocalAiManager", "Error initializing MediaPipe LlmInference: ${e.message}")
        }
    }

    fun generateResponse(prompt: String): String {
        return try {
            if (llmInference != null) {
                llmInference?.generateResponse(prompt) ?: "Modelo no disponible."
            } else {
                when {
                    prompt.contains("balance", ignoreCase = true) || prompt.contains("dinero", ignoreCase = true) ->
                        "Tu balance financiero se calcula en tiempo real con cada venta o gasto registrado en el Panel."
                    prompt.contains("cliente", ignoreCase = true) || prompt.contains("contacto", ignoreCase = true) ->
                        "Puedes registrar, buscar y organizar toda tu cartera de clientes desde la pestaña 'Clientes'."
                    else ->
                        "Hola, soy tu asistente Ethereal IA (Modo Offline). Para activar el modelo local completo de MediaPipe, coloca tu archivo model.bin en los directorios internos de la app. ¿En qué te ayudo con tu negocio hoy?"
                }
            }
        } catch (e: Exception) {
            "Error generando respuesta local: ${e.localizedMessage}"
        }
    }
}

sealed class ParsedCommand {
    data class AddTransaction(val transaction: TransactionEntity) : ParsedCommand()
    data class AddClient(val client: ClientEntity) : ParsedCommand()
    data class DeleteClient(val nameQuery: String) : ParsedCommand()
    data class Query(val intent: String) : ParsedCommand()
    object Unknown : ParsedCommand()
}

class LocalAiParser {

    fun parseInput(input: String): ParsedCommand {
        val cleanInput = input.trim()

        // 1. Detección de Eliminar / Borrar Cliente
        val isDelete = cleanInput.contains("borrar", ignoreCase = true) || cleanInput.contains("eliminar", ignoreCase = true) || cleanInput.contains("quitar", ignoreCase = true)
        if (isDelete && (cleanInput.contains("cliente", ignoreCase = true) || cleanInput.contains("a", ignoreCase = true))) {
            val words = cleanInput.split(" ")
            val stopWords = listOf("borrar", "eliminar", "quitar", "cliente", "a", "al", "el", "la")
            val nameQuery = words.filter { !stopWords.contains(it.lowercase()) }.joinToString(" ")
            if (nameQuery.isNotBlank()) {
                return ParsedCommand.DeleteClient(nameQuery)
            }
        }

        // 2. Detección de Transacciones (Ventas / Gastos)
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

        // 3. Detección de Agregar Cliente
        val isClient = cleanInput.contains("cliente", ignoreCase = true) || cleanInput.contains("agregar", ignoreCase = true) || cleanInput.contains("registrar", ignoreCase = true)
        if (isClient && !isDelete) {
            val phoneRegex = Regex("""(\+?\d{8,15})""")
            val phoneMatch = phoneRegex.find(cleanInput)
            val phone = phoneMatch?.value ?: ""

            val words = cleanInput.split(" ")
            val stopWords = listOf("cliente", "agregar", "registrar", "a", "con", "tel", "telefono", "teléfono", "nuevo", "nueva")
            val nameCandidates = words.filter { !stopWords.contains(it.lowercase()) && !it.matches(Regex("""\d+""")) }
            val name = nameCandidates.joinToString(" ").replaceFirstChar { it.uppercase() }

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

        // 4. Consultas Generales
        if (cleanInput.contains("balance", ignoreCase = true) || cleanInput.contains("cuanto", ignoreCase = true) || cleanInput.contains("resumen", ignoreCase = true)) {
            return ParsedCommand.Query("balance_summary")
        }

        return ParsedCommand.Unknown
    }
}
