package com.frange.coasters.di

import android.app.Application
import com.google.gson.Gson
import com.frange.coasters.data.api.service.QueueApiService
import com.frange.coasters.data.repository.queue.QueueRepository
import com.frange.coasters.data.repository.queue.QueueRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePoiRepository(
        application: Application,
        gson: Gson,
        service: QueueApiService
    ): QueueRepository = QueueRepositoryImpl(application, gson, service)

}