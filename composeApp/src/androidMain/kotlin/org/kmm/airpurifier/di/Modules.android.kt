package org.kmm.airpurifier.di

import getRoomDatabase
import org.kmm.airpurifier.database.getBLEDeviceDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { getRoomDatabase(getBLEDeviceDatabase(androidContext())).bleDeviceDao() }
}