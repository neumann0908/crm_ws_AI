package com.crm.chatflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crm.chatflow.data.local.dao.ClientDao
import com.crm.chatflow.data.local.dao.ProductDao
import com.crm.chatflow.data.local.entity.ClientEntity
import com.crm.chatflow.data.local.entity.MessageTemplateEntity
import com.crm.chatflow.data.local.entity.ProductEntity
import com.crm.chatflow.data.local.entity.QuotationEntity

@Database(
    entities = [
        ClientEntity::class,
        ProductEntity::class,
        QuotationEntity::class,
        MessageTemplateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chatflow_crm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
