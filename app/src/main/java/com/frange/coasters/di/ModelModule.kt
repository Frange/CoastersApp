package com.frange.coasters.di

import com.frange.coasters.data.api.park.ParkModel
import com.frange.coasters.data.api.park.ParkModelImpl
import com.frange.coasters.data.api.parkinfo.model.ParkInfoModel
import com.frange.coasters.data.api.parkinfo.model.ParkInfoModelImpl
import com.frange.coasters.data.api.park.ride.RideModel
import com.frange.coasters.data.api.park.ride.RideModelImpl
import com.frange.coasters.data.repository.queue.QueueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Singleton
    @Provides
    fun provideParkModel(
        repository: QueueRepository
    ): ParkInfoModel = ParkInfoModelImpl(repository)

    @Singleton
    @Provides
    fun provideCoasterModel(
        repository: QueueRepository
    ): ParkModel = ParkModelImpl(repository)

    @Singleton
    @Provides
    fun provideRideModel(
        repository: QueueRepository
    ): RideModel = RideModelImpl(repository)

}