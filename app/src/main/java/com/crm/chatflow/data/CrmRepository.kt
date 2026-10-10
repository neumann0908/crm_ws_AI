package com.crm.chatflow.data

import com.crm.chatflow.data.local.dao.ClientDao
import com.crm.chatflow.data.local.dao.TransactionDao
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class CrmRepository(
    private val clientDao: ClientDao,
    private val transactionDao: TransactionDao
) {
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val clientCount: Flow<Int> = clientDao.getClientCount()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: Flow<Double?> = transactionDao.getTotalIncome()
    val totalExpense: Flow<Double?> = transactionDao.getTotalExpense()

    suspend fun insertClient(client: ClientEntity) {
        clientDao.insertClient(client)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
}
