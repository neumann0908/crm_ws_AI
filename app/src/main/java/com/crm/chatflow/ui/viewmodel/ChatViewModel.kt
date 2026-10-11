package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import com.crm.chatflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isIncome: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CrmRepository

    val inputText = MutableStateFlow("")

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao(), db.reminderDao())
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(text = userText, isUser = true, isIncome = false))
        _messages.value = currentList
        inputText.value = ""

        _isProcessing.value = true

        viewModelScope.launch {
            val lower = userText.lowercase()
            val botResponse: String
            var isIncomeResponse = false

            if (lower.contains("venta") || lower.contains("ingreso") || lower.contains("pago")) {
                val amount = extractAmount(userText)
                repository.insertTransaction(
                    TransactionEntity(
                        title = userText,
                        amount = amount,
                        type = "INCOME"
                    )
                )
                botResponse = "¡Entendido! He registrado la venta de $$amount correctamente."
                isIncomeResponse = true
            } else if (lower.contains("gasto") || lower.contains("compra")) {
                val amount = extractAmount(userText)
                repository.insertTransaction(
                    TransactionEntity(
                        title = userText,
                        amount = amount,
                        type = "EXPENSE"
                    )
                )
                botResponse = "¡Entendido! He registrado el gasto de $$amount correctamente."
                isIncomeResponse = false
            } else {
                botResponse = "Procesado: '$userText'. Puedo registrar ventas o gastos si indicas el monto."
                isIncomeResponse = false
            }

            val updatedList = _messages.value.toMutableList()
            updatedList.add(ChatMessage(text = botResponse, isUser = false, isIncome = isIncomeResponse))
            _messages.value = updatedList
            _isProcessing.value = false
        }
    }

    private fun extractAmount(text: String): Double {
        val numbers = Regex("\\d+(\\.\\d+)?").findAll(text).map { it.value.toDouble() }.toList()
        return numbers.firstOrNull() ?: 10.0
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
