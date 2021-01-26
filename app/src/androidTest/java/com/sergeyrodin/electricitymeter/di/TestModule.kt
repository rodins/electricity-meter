package com.sergeyrodin.electricitymeter.di

import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class TestModule {
    @Singleton
    @Binds
    abstract fun bindDataSource(dataSource: FakeDataSource) : MeterDataSource
}