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
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    suspend fun insertClient(client: ClientEntity) = clientDao.insertClient(client)

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)

    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    suspend fun insertReminder(reminder: ReminderEntity) = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)
}
