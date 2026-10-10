package com.neuman.crm.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gestor de Inteligencia Artificial Local utilizando MediaPipe GenAI (LlmInference).
 * Diseñado para la ejecución offline en el CRM para solopreneurs.
 */
class LocalAiParser(private val context: Context) {

    private var llmInference: LlmInference? = null

    /**
     * Inicializa el modelo de lenguaje local a partir de la ruta del archivo de modelo (.task) en el dispositivo.
     */
    fun initialize(modelPath: String) {
        if (llmInference == null) {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(512)
                .setTemperature(0.7f)
                .build()
            
            llmInference = LlmInference.createFromOptions(context, options)
        }
    }

    /**
     * Genera una respuesta basada en el texto de entrada (prompt) de forma asíncrona.
     */
    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val inference = llmInference 
            ?: throw IllegalStateException("LlmInference no ha sido inicializado. Asegúrate de llamar a initialize(modelPath) antes de generar respuestas.")
        
        try {
            inference.generateResponse(prompt) ?: "No se pudo generar una respuesta vacía."
        } catch (e: Exception) {
            "Error en la inferencia local: ${e.localizedMessage}"
        }
    }

    /**
     * Libera los recursos del modelo cuando ya no se necesiten.
     */
    fun close() {
        llmInference?.close()
        llmInference = null
    }
}
