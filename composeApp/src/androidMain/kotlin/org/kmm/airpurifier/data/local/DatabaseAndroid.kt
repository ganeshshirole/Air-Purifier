package org.kmm.airpurifier.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getBLEDeviceDatabase(ctx: Context): RoomDatabase.Builder<BLEDeviceDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("ble_device.db")
    return Room.databaseBuilder<BLEDeviceDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}