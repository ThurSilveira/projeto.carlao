package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escala.ministerial.core.data.database.entity.MinistroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MinistroDao {
    @Query("SELECT * FROM ministros ORDER BY nome ASC")
    fun observeAll(): Flow<List<MinistroEntity>>

    @Query("SELECT * FROM ministros WHERE id = :id")
    suspend fun findById(id: Long): MinistroEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(ministros: List<MinistroEntity>)

    @Query("DELETE FROM ministros WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ministros")
    suspend fun deleteAll()
}
