package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.escala.ministerial.core.data.database.entity.IndisponibilidadeEntity

@Dao
interface IndisponibilidadeDao {
    @Query("SELECT * FROM indisponibilidades WHERE ministroId = :ministroId ORDER BY data ASC")
    suspend fun findByMinistroId(ministroId: Long): List<IndisponibilidadeEntity>

    @Upsert
    suspend fun upsertAll(items: List<IndisponibilidadeEntity>)

    @Query("DELETE FROM indisponibilidades WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM indisponibilidades WHERE ministroId = :ministroId")
    suspend fun deleteByMinistroId(ministroId: Long)
}
