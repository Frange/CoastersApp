package com.frange.coasters.di

import com.frange.coasters.data.api.model.coaster.CoasterModel
import com.frange.coasters.data.api.model.coaster.CoasterModelImpl
import com.frange.coasters.data.api.model.company.CompanyModel
import com.frange.coasters.data.api.model.company.CompanyModelImpl
import com.frange.coasters.data.api.model.park.ParkModel
import com.frange.coasters.data.api.model.park.ParkModelImpl
import com.frange.coasters.data.api.model.ride.RideModel
import com.frange.coasters.data.api.model.ride.RideModelImpl
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
    fun provideCompanyModel(
        repository: QueueRepository
    ): CompanyModel = CompanyModelImpl(repository)

    @Singleton
    @Provides
    fun provideParkModel(
        repository: QueueRepository
    ): ParkModel = ParkModelImpl(repository)

    @Singleton
    @Provides
    fun provideCoasterModel(
        repository: QueueRepository
    ): CoasterModel = CoasterModelImpl(repository)

    @Singleton
    @Provides
    fun provideRideModel(
        repository: QueueRepository
    ): RideModel = RideModelImpl(repository)

}