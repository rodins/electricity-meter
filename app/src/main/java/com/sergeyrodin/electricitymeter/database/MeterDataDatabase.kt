package com.sergeyrodin.electricitymeter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities=[MeterData::class, PaidDate::class, Price::class], version = 3, exportSchema = true)
abstract class MeterDataDatabase: RoomDatabase() {
    abstract val meterDataDatabaseDao: MeterDataDatabaseDao

    companion object {
        val MIGRATION_1_2 = object:Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS paid_dates (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL)")
            }

        }

        val MIGRATION_2_3 = object:Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS prices (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `price` REAL NOT NULL)")
            }
        }
    }
}