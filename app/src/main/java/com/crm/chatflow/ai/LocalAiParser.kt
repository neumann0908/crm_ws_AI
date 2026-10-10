package com.crm.chatflow.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalAiParser(private val context: Context) {

    private var llmInference: LlmInference? = null

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

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val inference = llmInference 
            ?: throw IllegalStateException("LlmInference no ha sido inicializado. Llama a initialize(modelPath) primero.")
        
        try {
            inference.generateResponse(prompt) ?: "Respuesta vacía"
        } catch (e: Exception) {
            "Error en la inferencia local: ${e.localizedMessage}"
        }
    }

    fun close() {
        llmInference?.close()
        llmInference = null
    }
}
