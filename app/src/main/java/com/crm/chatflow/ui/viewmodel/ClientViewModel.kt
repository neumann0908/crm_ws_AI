package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import com.crm.chatflow.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CrmRepository
    val clients: StateFlow<List<ClientEntity>>

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao(), db.reminderDao())
        clients = repository.allClients.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addClient(name: String, phone: String, email: String, notes: String) {
        viewModelScope.launch {
            repository.insertClient(ClientEntity(name = name, phone = phone, email = email, notes = notes, status = "ACTIVE"))
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientViewModel::class.java)) return ClientViewModel(application) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
