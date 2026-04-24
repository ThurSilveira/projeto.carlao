package com.escala.ministerial.feature.eventos.data.di

import com.escala.ministerial.feature.eventos.data.datasource.EventoApiService
import com.escala.ministerial.feature.eventos.data.repository.EventoRepositoryImpl
import com.escala.ministerial.feature.eventos.domain.repository.EventoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventosModule {

    @Binds
    @Singleton
    abstract fun bindEventoRepository(impl: EventoRepositoryImpl): EventoRepository

    companion object {
        @Provides
        @Singleton
        fun provideEventoApiService(retrofit: Retrofit): EventoApiService =
            retrofit.create(EventoApiService::class.java)
    }
}
