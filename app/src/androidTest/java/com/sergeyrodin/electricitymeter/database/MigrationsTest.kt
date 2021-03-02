package com.sergeyrodin.electricitymeter.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DB_NAME = "test-db"
private val METER_DATA = MeterData(15887)
private val PAID_DATE = PaidDate(1, METER_DATA.date)
private val PRICE = Price(1, 1.68)

@RunWith(AndroidJUnit4::class)
@SmallTest
class MigrationsTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MeterDataDatabase::class.java.canonicalName
    )

    @Test
    fun migrationFrom1To2_containsCorrectData() {
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 1)
        insertMeterData(METER_DATA, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            2,
            false,
            MeterDataDatabase.MIGRATION_1_2
        )

        val dao = getMigratedRoomDatabase().meterDataDatabaseDao

        val meterData = dao.getMeterDataByIdBlocking(METER_DATA.id)
        assertThat(meterData, `is`(METER_DATA))

        dao.insertPaidDateBlocking(PAID_DATE)
        val paidDate = dao.getPaidDateByIdBlocking(PAID_DATE.id)
        assertThat(paidDate,`is`(PAID_DATE))
    }

    private fun insertMeterData(meterData: MeterData, db: SupportSQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put("id", meterData.id)
        contentValues.put("data", meterData.data)
        contentValues.put("date", meterData.date)
        db.insert("meter_data", SQLiteDatabase.CONFLICT_REPLACE, contentValues)
    }

    private fun getMigratedRoomDatabase(): MeterDataDatabase {
        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MeterDataDatabase::class.java,
            TEST_DB_NAME)
            .addMigrations(MeterDataDatabase.MIGRATION_1_2, MeterDataDatabase.MIGRATION_2_3)
            .build()
        migrationTestHelper.closeWhenFinished(database)
        return database
    }

    @Test
    fun migrationFrom2to3_containsCorrectData() {
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 2)
        insertMeterData(METER_DATA, db)
        insertPaidDate(PAID_DATE, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            false,
            MeterDataDatabase.MIGRATION_2_3
        )

        val dao = getMigratedRoomDatabase().meterDataDatabaseDao

        val meterData = dao.getMeterDataByIdBlocking(METER_DATA.id)
        assertThat(meterData, `is`(METER_DATA))

        val paidDate = dao.getPaidDateByIdBlocking(PAID_DATE.id)
        assertThat(paidDate, `is`(PAID_DATE))

        dao.insertPriceBlocking(PRICE)
        val price = dao.getPriceByIdBlocking(PRICE.id)
        assertThat(price, `is`(PRICE))
    }

    private fun insertPaidDate(paidDate: PaidDate, db: SupportSQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put("id", paidDate.id)
        contentValues.put("date", paidDate.date)
        db.insert("paid_dates", SQLiteDatabase.CONFLICT_REPLACE, contentValues)
    }

}