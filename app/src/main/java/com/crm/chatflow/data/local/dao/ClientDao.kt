package com.crm.chatflow.data.local.dao

import androidx.room.*
import com.crm.chatflow.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY id DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT COUNT(*) FROM clients")
    fun getClientCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)
}
