package com.escala.ministerial.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.escala.ministerial.core.data.database.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedbacks ORDER BY dataEnvio DESC")
    fun observeAll(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(feedbacks: List<FeedbackEntity>)

    @Query("DELETE FROM feedbacks")
    suspend fun deleteAll()
}
