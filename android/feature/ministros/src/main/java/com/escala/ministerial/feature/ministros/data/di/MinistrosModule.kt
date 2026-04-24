package com.escala.ministerial.feature.ministros.data.di

import com.escala.ministerial.feature.ministros.data.datasource.MinistroApiService
import com.escala.ministerial.feature.ministros.data.repository.MinistroRepositoryImpl
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MinistrosModule {

    @Binds
    @Singleton
    abstract fun bindMinistroRepository(impl: MinistroRepositoryImpl): MinistroRepository

    companion object {
        @Provides
        @Singleton
        fun provideMinistroApiService(retrofit: Retrofit): MinistroApiService =
            retrofit.create(MinistroApiService::class.java)
    }
}
