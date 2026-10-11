package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import com.crm.chatflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CrmRepository

    val totalIncome: StateFlow<Double>
    val totalExpense: StateFlow<Double>
    val totalBalance: StateFlow<Double>

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao())

        totalIncome = repository.totalIncome
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalExpense = repository.totalExpense
            .map { it ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        totalBalance = repository.allTransactions
            .map { transactions ->
                val income = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
                val expense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                income - expense
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    fun addTransaction(title: String, amount: Double, type: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    type = type
                )
            )
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
