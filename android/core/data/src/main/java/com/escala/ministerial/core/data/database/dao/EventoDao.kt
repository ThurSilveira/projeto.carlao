package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escala.ministerial.core.data.database.entity.EventoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos ORDER BY data ASC")
    fun observeAll(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM eventos WHERE id = :id")
    suspend fun findById(id: Long): EventoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(eventos: List<EventoEntity>)

    @Query("DELETE FROM eventos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM eventos")
    suspend fun deleteAll()
}
