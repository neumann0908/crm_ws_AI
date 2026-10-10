package com.crm.chatflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crm.chatflow.data.local.dao.ClientDao
import com.crm.chatflow.data.local.dao.TransactionDao
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.TransactionEntity

@Database(entities = [ClientEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class CrmDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: CrmDatabase? = null

        fun getDatabase(context: Context): CrmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrmDatabase::class.java,
                    "crm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
