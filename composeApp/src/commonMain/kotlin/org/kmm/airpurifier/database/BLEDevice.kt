package org.kmm.airpurifier.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bledevice",
    indices = [Index(value = ["address"], unique = true)]
)
data class BLEDevice(
    val name: String,
    val address: String,
    val dateTime: Long,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)