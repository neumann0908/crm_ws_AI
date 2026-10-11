package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CrmRepository

    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao(), db.reminderDao())

        totalIncome = repository.allTransactions
            .map { list -> list.filter { it.type == "INCOME" }.sumOf { it.amount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalExpense = repository.allTransactions
            .map { list -> list.filter { it.type == "EXPENSE" }.sumOf { it.amount } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }
}
