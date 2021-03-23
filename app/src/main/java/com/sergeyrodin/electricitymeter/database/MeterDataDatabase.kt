package com.sergeyrodin.electricitymeter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities=[MeterData::class, PaidDate::class, Price::class], version = 4, exportSchema = true)
abstract class MeterDataDatabase: RoomDatabase() {
    abstract val meterDataDatabaseDao: MeterDataDatabaseDao

    companion object {
        val MIGRATION_1_2 = object:Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                createTablePaidDates(database)
            }

            private fun createTablePaidDates(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS paid_dates (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date INTEGER NOT NULL)")
            }

        }

        val MIGRATION_2_3 = object:Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                createTablePrices(database)
            }

            private fun createTablePrices(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS prices (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, price REAL NOT NULL)")
            }
        }

        val MIGRATION_3_4 = object:Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                createTablePaidDatesNew(database)
                createIndexPaidDatesNewPriceId(database)
                copyFromPaidDatesToPaidDatesNewWithPriceId(database)
                deletePaidDates(database)
                renamePaidDatesNewToPaidDates(database)
            }

            private fun createTablePaidDatesNew(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS paid_dates_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date INTEGER NOT NULL, price_id INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(price_id) REFERENCES prices(id) ON UPDATE NO ACTION ON DELETE SET DEFAULT )"
                )
            }

            private fun createIndexPaidDatesNewPriceId(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE INDEX IF NOT EXISTS index_paid_dates_price_id ON paid_dates_new (price_id)")
            }

            private fun copyFromPaidDatesToPaidDatesNewWithPriceId(database: SupportSQLiteDatabase) {
                database.execSQL("INSERT INTO paid_dates_new (id, date) SELECT id, date FROM paid_dates")
            }

            private fun deletePaidDates(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE paid_dates")
            }

            private fun renamePaidDatesNewToPaidDates(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE paid_dates_new RENAME TO paid_dates")
            }

        }
    }
}