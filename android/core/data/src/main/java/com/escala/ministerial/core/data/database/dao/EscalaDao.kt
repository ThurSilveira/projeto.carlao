package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escala.ministerial.core.data.database.entity.EscalaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EscalaDao {
    @Query("SELECT * FROM escalas ORDER BY eventoData ASC")
    fun observeAll(): Flow<List<EscalaEntity>>

    @Query("SELECT * FROM escalas WHERE id = :id")
    suspend fun findById(id: Long): EscalaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(escalas: List<EscalaEntity>)

    @Query("DELETE FROM escalas WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM escalas")
    suspend fun deleteAll()
}
