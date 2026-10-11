package com.crm.chatflow.ai

import android.content.Context
import android.util.Log
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
                // Respuesta de respaldo (Fallback) offline para solopreneurs
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
