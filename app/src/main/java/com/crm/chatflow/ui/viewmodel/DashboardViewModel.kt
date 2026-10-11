package com.crm.chatflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crm.chatflow.data.CrmRepository
import com.crm.chatflow.data.local.database.CrmDatabase
import com.crm.chatflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TransactionFilter { ALL, INCOME, EXPENSE }

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CrmRepository

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(TransactionFilter.ALL)

    val transactions: StateFlow<List<TransactionEntity>>
    val totalIncome: StateFlow<Double?>
    val totalExpense: StateFlow<Double?>

    init {
        val db = CrmDatabase.getDatabase(application)
        repository = CrmRepository(db.clientDao(), db.transactionDao())

        totalIncome = repository.totalIncome.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        totalExpense = repository.totalExpense.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        // Combinar la lista base con el filtro de texto y tipo
        transactions = combine(
            repository.allTransactions,
            searchQuery,
            selectedFilter
        ) { list, query, filter ->
            list.filter { item ->
                val matchesQuery = item.title.contains(query, ignoreCase = true)
                val matchesFilter = when (filter) {
                    TransactionFilter.ALL -> true
                    TransactionFilter.INCOME -> item.type == "INCOME"
                    TransactionFilter.EXPENSE -> item.type == "EXPENSE"
                }
                matchesQuery && matchesFilter
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
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
