package com.escala.ministerial.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.escala.ministerial.core.data.database.converter.DateConverter
import com.escala.ministerial.core.data.database.dao.EscalaDao
import com.escala.ministerial.core.data.database.dao.EventoDao
import com.escala.ministerial.core.data.database.dao.FeedbackDao
import com.escala.ministerial.core.data.database.dao.IndisponibilidadeDao
import com.escala.ministerial.core.data.database.dao.LogAuditoriaDao
import com.escala.ministerial.core.data.database.dao.MinistroDao
import com.escala.ministerial.core.data.database.entity.EscalaEntity
import com.escala.ministerial.core.data.database.entity.EventoEntity
import com.escala.ministerial.core.data.database.entity.FeedbackEntity
import com.escala.ministerial.core.data.database.entity.IndisponibilidadeEntity
import com.escala.ministerial.core.data.database.entity.LogAuditoriaEntity
import com.escala.ministerial.core.data.database.entity.MinistroEntity

@Database(
    entities = [
        MinistroEntity::class,
        EventoEntity::class,
        EscalaEntity::class,
        FeedbackEntity::class,
        LogAuditoriaEntity::class,
        IndisponibilidadeEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(DateConverter::class)
abstract class EscalaDatabase : RoomDatabase() {
    abstract fun ministroDao(): MinistroDao
    abstract fun eventoDao(): EventoDao
    abstract fun escalaDao(): EscalaDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun logAuditoriaDao(): LogAuditoriaDao
    abstract fun indisponibilidadeDao(): IndisponibilidadeDao
}
