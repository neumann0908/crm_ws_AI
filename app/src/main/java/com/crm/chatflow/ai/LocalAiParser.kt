package com.crm.chatflow.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llm.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalAiParser(private val context: Context) {

    private var llmInference: LlmInference? = null

    // Inicializa el modelo local (ej: Gemma o Phi en formato MediaPipe task ubicado en assets)
    fun initializeModel(modelPath: String) {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(512)
                .setTemperature(0.2f)
                .build()
            
            llmInference = LlmInference.createFromOptions(context, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Parsea texto de chat para extraer intención o datos de cliente de forma offline
    suspend fun parseChatText(chatMessage: String): String = withContext(Dispatchers.IO) {
        val inference = llmInference ?: return@withContext "Modelo de IA no inicializado"
        try {
            val prompt = "Extrae el nombre, producto de interés y urgencia de este mensaje de cliente: \"$chatMessage\""
            return@withContext inference.generateResponse(prompt)
        } catch (e: Exception) {
            return@withContext "Error al procesar con IA local: ${e.localizedMessage}"
        }
    }
}
