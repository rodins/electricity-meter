package com.sergeyrodin.electricitymeter

import android.app.Application
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ElectricityMeterApplication : Application()