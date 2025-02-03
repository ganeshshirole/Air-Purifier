package org.kmm.airpurifier.di

import getRoomDatabase
import org.kmm.airpurifier.data.local.getBLEDeviceDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { getRoomDatabase(getBLEDeviceDatabase()).bleDeviceDao() }
}