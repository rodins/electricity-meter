package com.sergeyrodin.electricitymeter.di

import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.datasource.RoomMeterDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class MeterDataSourceModule {

    @Singleton
    @Binds
    abstract fun provideDataSource(dataSource: RoomMeterDataSource): MeterDataSource

}