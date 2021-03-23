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
private val PRICE = Price(1, 1.68)
private val PAID_DATE = PaidDate(1, METER_DATA.date)

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
        insertMeterData(METER_DATA.id, METER_DATA.data, METER_DATA.date, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            2,
            false,
            MeterDataDatabase.MIGRATION_1_2
        )

        val dao = getMigratedRoomDatabase().meterDataDatabaseDao

        val meterData = dao.getMeterDataByIdBlocking(METER_DATA.id)
        assertThat(meterData?.data, `is`(METER_DATA.data))
        assertThat(meterData?.date, `is`(METER_DATA.date))
    }

    private fun insertMeterData(id: Int, data: Int, date: Long, db: SupportSQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("data", data)
        contentValues.put("date", date)
        db.insert("meter_data", SQLiteDatabase.CONFLICT_REPLACE, contentValues)
    }

    private fun getMigratedRoomDatabase(): MeterDataDatabase {
        val database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MeterDataDatabase::class.java,
            TEST_DB_NAME)
            .addMigrations(
                MeterDataDatabase.MIGRATION_1_2,
                MeterDataDatabase.MIGRATION_2_3,
                MeterDataDatabase.MIGRATION_3_4
            )
            .build()
        migrationTestHelper.closeWhenFinished(database)
        return database
    }

    @Test
    fun migrationFrom2to3_containsCorrectData() {
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 2)
        insertMeterData(METER_DATA.id, METER_DATA.data, METER_DATA.date, db)
        insertPaidDate(PAID_DATE.id, PAID_DATE.date, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            false,
            MeterDataDatabase.MIGRATION_2_3
        )

        val dao = getMigratedRoomDatabase().meterDataDatabaseDao

        val meterData = dao.getMeterDataByIdBlocking(METER_DATA.id)
        assertThat(meterData?.data, `is`(METER_DATA.data))
        assertThat(meterData?.date, `is`(METER_DATA.date))

        val paidDate = dao.getPaidDateByIdBlocking(PAID_DATE.id)
        assertThat(paidDate?.date, `is`(PAID_DATE.date))
    }

    private fun insertPaidDate(id: Int, date: Long, db: SupportSQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("date", date)
        db.insert("paid_dates", SQLiteDatabase.CONFLICT_REPLACE, contentValues)
    }

    @Test
    fun migrationsFrom3To4_containsCorrectData() {
        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 3)
        insertMeterData(METER_DATA.id, METER_DATA.data, METER_DATA.date, db)
        insertPaidDate(PAID_DATE.id, PAID_DATE.date, db)
        insertPrice(PRICE.id, PRICE.price, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            4,
            false,
            MeterDataDatabase.MIGRATION_3_4
        )

        val dao = getMigratedRoomDatabase().meterDataDatabaseDao

        val meterData = dao.getMeterDataByIdBlocking(METER_DATA.id)
        assertThat(meterData?.data, `is`(METER_DATA.data))
        assertThat(meterData?.date, `is`(METER_DATA.date))

        val paidDate = dao.getPaidDateByIdBlocking(PAID_DATE.id)
        assertThat(paidDate?.date, `is`(PAID_DATE.date))
        assertThat(paidDate?.priceId, `is`(0))

        val price = dao.getPriceByIdBlocking(PRICE.id)
        assertThat(price?.price, `is`(PRICE.price))
    }

    private fun insertPrice(id: Int, price: Double, db: SupportSQLiteDatabase) {
        val contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("price", price)
        db.insert("prices", SQLiteDatabase.CONFLICT_REPLACE, contentValues)
    }

}