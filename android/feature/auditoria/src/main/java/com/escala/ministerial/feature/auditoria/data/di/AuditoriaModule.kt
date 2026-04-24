package com.escala.ministerial.feature.auditoria.data.di

import com.escala.ministerial.feature.auditoria.data.datasource.AuditoriaApiService
import com.escala.ministerial.feature.auditoria.data.repository.AuditoriaRepositoryImpl
import com.escala.ministerial.feature.auditoria.domain.repository.AuditoriaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuditoriaModule {

    @Binds
    @Singleton
    abstract fun bindAuditoriaRepository(impl: AuditoriaRepositoryImpl): AuditoriaRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuditoriaApiService(retrofit: Retrofit): AuditoriaApiService =
            retrofit.create(AuditoriaApiService::class.java)
    }
}
