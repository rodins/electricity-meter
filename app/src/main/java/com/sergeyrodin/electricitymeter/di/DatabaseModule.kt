package com.sergeyrodin.electricitymeter.di

import android.content.Context
import androidx.room.Room
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MeterDataDatabase {
        return Room.databaseBuilder(
            appContext,
            MeterDataDatabase::class.java,
            "meter_data_database"
        )
            .build()
    }

    @Provides
    fun provideMeterDatabaseDao(database: MeterDataDatabase) : MeterDataDatabaseDao {
        return database.meterDataDatabaseDao
    }
}