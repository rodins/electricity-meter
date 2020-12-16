package com.sergeyrodin.electricitymeter

import android.app.Application
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class ElectricityMeterApplication : Application() {
    val meterDataSource: MeterDataSource
        get() = ServiceLocator.provideMeterDataSource(this)
}