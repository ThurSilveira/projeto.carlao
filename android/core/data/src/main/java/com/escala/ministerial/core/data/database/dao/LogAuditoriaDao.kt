package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escala.ministerial.core.data.database.entity.LogAuditoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogAuditoriaDao {
    @Query("SELECT * FROM logs_auditoria ORDER BY dataHora DESC")
    fun observeAll(): Flow<List<LogAuditoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(logs: List<LogAuditoriaEntity>)

    @Query("DELETE FROM logs_auditoria")
    suspend fun deleteAll()
}
