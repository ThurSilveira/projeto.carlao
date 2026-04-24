package com.escala.ministerial.core.data.di

import android.content.Context
import androidx.room.Room
import com.escala.ministerial.core.data.database.EscalaDatabase
import com.escala.ministerial.core.data.database.dao.EscalaDao
import com.escala.ministerial.core.data.database.dao.EventoDao
import com.escala.ministerial.core.data.database.dao.FeedbackDao
import com.escala.ministerial.core.data.database.dao.LogAuditoriaDao
import com.escala.ministerial.core.data.database.dao.MinistroDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EscalaDatabase =
        Room.databaseBuilder(context, EscalaDatabase::class.java, "escala_ministerial.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMinistroDao(db: EscalaDatabase): MinistroDao = db.ministroDao()

    @Provides
    fun provideEventoDao(db: EscalaDatabase): EventoDao = db.eventoDao()

    @Provides
    fun provideEscalaDao(db: EscalaDatabase): EscalaDao = db.escalaDao()

    @Provides
    fun provideFeedbackDao(db: EscalaDatabase): FeedbackDao = db.feedbackDao()

    @Provides
    fun provideLogAuditoriaDao(db: EscalaDatabase): LogAuditoriaDao = db.logAuditoriaDao()
}
