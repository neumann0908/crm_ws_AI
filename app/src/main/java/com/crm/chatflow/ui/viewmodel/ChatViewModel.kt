package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.ai.LocalAiManager
import com.crm.chatflow.ai.LocalAiParser
import com.crm.chatflow.ai.ParsedCommand
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CrmRepository
    private val aiManager = LocalAiManager(application)
    private val parser = LocalAiParser()

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("¡Hola! Soy tu asistente de IA local. Puedes decirme cosas como:\n• 'Registrar venta de 50$'\n• 'Agregar cliente Carlos Ruiz 04121234567'", false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao())
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(prompt, true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val command = parser.parseInput(prompt)
            var responseText = ""

            when (command) {
                is ParsedCommand.AddTransaction -> {
                    repository.insertTransaction(command.transaction)
                    val tipoStr = if (command.transaction.type == "INCOME") "Venta" else "Gasto"
                    responseText = "✅ ¡Listo! Se ha registrado $tipoStr por $${String.format("%.2f", command.transaction.amount)} en el Panel."
                }
                is ParsedCommand.AddClient -> {
                    repository.insertClient(command.client)
                    responseText = "👤 ¡Cliente guardado! '${command.client.name}' ha sido agregado a tu lista de Clientes."
                }
                is ParsedCommand.Query -> {
                    val income = repository.totalIncome.first() ?: 0.0
                    val expense = repository.totalExpense.first() ?: 0.0
                    val balance = income - expense
                    responseText = "📊 Resumen Financiero Actual:\n\n• Balance: $${String.format("%.2f", balance)}\n• Ingresos: +$${String.format("%.2f", income)}\n• Gastos: -$${String.format("%.2f", expense)}"
                }
                is ParsedCommand.Unknown -> {
                    responseText = aiManager.generateResponse(prompt)
                }
            }

            withContext(Dispatchers.Main) {
                _messages.value = _messages.value + ChatMessage(responseText, false)
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
