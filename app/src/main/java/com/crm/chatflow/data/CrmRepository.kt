package com.crm.chatflow.data

import com.crm.chatflow.data.local.dao.ClientDao
import com.crm.chatflow.data.local.dao.ReminderDao
import com.crm.chatflow.data.local.dao.TransactionDao
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.ReminderEntity
import com.crm.chatflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class CrmRepository(
    private val clientDao: ClientDao,
    private val transactionDao: TransactionDao,
    private val reminderDao: ReminderDao
) {
    // Clientes
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    suspend fun insertClient(client: ClientEntity) = clientDao.insertClient(client)
    suspend fun deleteClient(client: ClientEntity) = clientDao.deleteClient(client)

    // Transacciones
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: Flow<Double?> = transactionDao.getTotalIncome()
    val totalExpense: Flow<Double?> = transactionDao.getTotalExpense()
    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)

    // Recordatorios & Agenda
    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    val pendingReminders: Flow<List<ReminderEntity>> = reminderDao.getPendingReminders()
    suspend fun insertReminder(reminder: ReminderEntity) = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)
}
