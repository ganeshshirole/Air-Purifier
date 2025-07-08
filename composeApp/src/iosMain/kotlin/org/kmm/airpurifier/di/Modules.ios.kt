package org.kmm.airpurifier.di

import org.kmm.airpurifier.data.local.getBLEDeviceDatabase
import org.kmm.airpurifier.data.local.getRoomDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { getRoomDatabase(getBLEDeviceDatabase()).bleDeviceDao() }
}