package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.ai.LocalAiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val aiManager = LocalAiManager(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("¡Hola! Soy tu asistente de IA local con MediaPipe. Pregúntame lo que necesites sobre tu negocio.", false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(prompt, true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val responseText = aiManager.generateResponse(prompt)
            withContext(Dispatchers.Main) {
                val aiMessage = ChatMessage(responseText, false)
                _messages.value = _messages.value + aiMessage
                _isLoading.value = false
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
