package com.escala.ministerial.feature.escalas.data.di

import com.escala.ministerial.feature.escalas.data.datasource.EscalaApiService
import com.escala.ministerial.feature.escalas.data.repository.EscalaRepositoryImpl
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EscalasModule {

    @Binds
    @Singleton
    abstract fun bindEscalaRepository(impl: EscalaRepositoryImpl): EscalaRepository

    companion object {
        @Provides
        @Singleton
        fun provideEscalaApiService(retrofit: Retrofit): EscalaApiService =
            retrofit.create(EscalaApiService::class.java)
    }
}
