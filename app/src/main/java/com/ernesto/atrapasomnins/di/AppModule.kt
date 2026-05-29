package com.ernesto.atrapasomnins.di

import android.content.Context
import com.ernesto.atrapasomnins.data.repository.EtiquetaRepository
import com.ernesto.atrapasomnins.data.repository.SuenoRepository
import com.ernesto.atrapasomnins.data.storage.JsonStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJsonStorage(@ApplicationContext context: Context): JsonStorage {
        return JsonStorage(context)
    }

    @Provides
    @Singleton
    fun provideSuenoRepository(storage: JsonStorage): SuenoRepository {
        return SuenoRepository(storage)
    }

    @Provides
    @Singleton
    fun provideEtiquetaRepository(storage: JsonStorage): EtiquetaRepository {
        return EtiquetaRepository(storage)
    }
}
